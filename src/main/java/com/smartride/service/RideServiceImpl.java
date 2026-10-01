package com.smartride.service;

import com.smartride.dto.CreateRideRequest;
import com.smartride.dto.RideAuditLogDTO;
import com.smartride.dto.RideResponseDTO;
import com.smartride.dto.UpdateStatusRequest;
import com.smartride.exception.InvalidStateTransitionException;
import com.smartride.exception.ResourceNotFoundException;
import com.smartride.model.Ride;
import com.smartride.model.RideAuditLog;
import com.smartride.model.RideStatus;
import com.smartride.model.VehicleType;
import com.smartride.repository.RideAuditLogRepository;
import com.smartride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideAuditLogRepository auditLogRepository;

    @Autowired
    public RideServiceImpl(RideRepository rideRepository, RideAuditLogRepository auditLogRepository) {
        this.rideRepository = rideRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public RideResponseDTO createRide(CreateRideRequest request) {
        BigDecimal fare = request.getCustomFare();
        if (fare == null || fare.compareTo(BigDecimal.ZERO) <= 0) {
            fare = calculateEstimatedFare(
                request.getSourceLocation(),
                request.getDestinationLocation(),
                request.getRequestedSeats(),
                request.getVehicleType().name()
            );
        }

        Ride ride = new Ride(
            request.getPassengerName(),
            request.getPassengerPhone(),
            request.getSourceLocation(),
            request.getDestinationLocation(),
            request.getDepartureTime() != null ? request.getDepartureTime() : LocalDateTime.now().plusMinutes(15),
            request.getRequestedSeats(),
            request.getVehicleType(),
            fare
        );
        ride.setStatus(RideStatus.REQUESTED);

        Ride savedRide = rideRepository.save(ride);

        // Record Initial Audit Log
        RideAuditLog auditLog = new RideAuditLog(
            savedRide.getId(),
            null,
            RideStatus.REQUESTED,
            "PASSENGER",
            savedRide.getPassengerName(),
            "Ride booking created with status REQUESTED"
        );
        auditLogRepository.save(auditLog);

        return enrichWithAuditTrail(savedRide);
    }

    @Override
    @Transactional(readOnly = true)
    public RideResponseDTO getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + id));
        return enrichWithAuditTrail(ride);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponseDTO> getAllRides() {
        return rideRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(RideResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponseDTO> getRidesByStatus(RideStatus status) {
        return rideRepository.findByStatusOrderByCreatedAtDesc(status).stream()
            .map(RideResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponseDTO> searchRides(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllRides();
        }
        return rideRepository.searchRides(query.trim()).stream()
            .map(RideResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public RideResponseDTO updateRideStatus(Long id, UpdateStatusRequest request) {
        Ride ride = rideRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + id));

        RideStatus currentStatus = ride.getStatus();
        RideStatus targetStatus = request.getTargetStatus();

        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new InvalidStateTransitionException(currentStatus, targetStatus);
        }

        ride.setStatus(targetStatus);

        if (request.getDriverName() != null && !request.getDriverName().isBlank()) {
            ride.setDriverName(request.getDriverName());
        }
        if (request.getDriverVehicleNumber() != null && !request.getDriverVehicleNumber().isBlank()) {
            ride.setDriverVehicleNumber(request.getDriverVehicleNumber());
        }

        Ride updatedRide = rideRepository.save(ride);

        String note = request.getNotes();
        if (note == null || note.isBlank()) {
            note = "Transitioned status from " + currentStatus + " to " + targetStatus;
        }

        RideAuditLog auditLog = new RideAuditLog(
            updatedRide.getId(),
            currentStatus,
            targetStatus,
            request.getActorRole(),
            request.getActorName() != null ? request.getActorName() : request.getActorRole(),
            note
        );
        auditLogRepository.save(auditLog);

        return enrichWithAuditTrail(updatedRide);
    }

    @Override
    public BigDecimal calculateEstimatedFare(String source, String destination, Integer seats, String vehicleTypeStr) {
        int charSum = Math.abs((source + destination).hashCode() % 35) + 10;
        BigDecimal baseRate = new BigDecimal("12.50");
        BigDecimal distanceFactor = BigDecimal.valueOf(charSum).multiply(new BigDecimal("1.75"));
        BigDecimal seatMultiplier = BigDecimal.valueOf(seats != null ? seats : 1).multiply(new BigDecimal("0.35")).add(BigDecimal.ONE);

        BigDecimal vehicleMultiplier = BigDecimal.ONE;
        try {
            if (vehicleTypeStr != null) {
                VehicleType vt = VehicleType.valueOf(vehicleTypeStr.toUpperCase());
                vehicleMultiplier = vt.getFareMultiplier();
            }
        } catch (Exception ignored) {}

        return baseRate.add(distanceFactor)
            .multiply(seatMultiplier)
            .multiply(vehicleMultiplier)
            .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void resetToSeedData() {
        auditLogRepository.deleteAll();
        rideRepository.deleteAll();
    }

    private RideResponseDTO enrichWithAuditTrail(Ride ride) {
        RideResponseDTO dto = RideResponseDTO.fromEntity(ride);
        List<RideAuditLogDTO> auditLogs = auditLogRepository.findByRideIdOrderByTimestampAsc(ride.getId()).stream()
            .map(RideAuditLogDTO::fromEntity)
            .collect(Collectors.toList());
        dto.setAuditTrail(auditLogs);
        return dto;
    }
}
