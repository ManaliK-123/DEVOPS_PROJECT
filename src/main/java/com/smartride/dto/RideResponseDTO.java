package com.smartride.dto;

import com.smartride.model.Ride;
import com.smartride.model.RideStatus;
import com.smartride.model.VehicleType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RideResponseDTO {

    private Long id;
    private String passengerName;
    private String passengerPhone;
    private String sourceLocation;
    private String destinationLocation;
    private LocalDateTime departureTime;
    private Integer requestedSeats;
    private VehicleType vehicleType;
    private String vehicleDisplayName;
    private BigDecimal estimatedFare;
    private RideStatus status;
    private String driverName;
    private String driverVehicleNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RideAuditLogDTO> auditTrail = new ArrayList<>();

    public RideResponseDTO() {
    }

    public static RideResponseDTO fromEntity(Ride ride) {
        RideResponseDTO dto = new RideResponseDTO();
        dto.setId(ride.getId());
        dto.setPassengerName(ride.getPassengerName());
        dto.setPassengerPhone(ride.getPassengerPhone());
        dto.setSourceLocation(ride.getSourceLocation());
        dto.setDestinationLocation(ride.getDestinationLocation());
        dto.setDepartureTime(ride.getDepartureTime());
        dto.setRequestedSeats(ride.getRequestedSeats());
        dto.setVehicleType(ride.getVehicleType());
        dto.setVehicleDisplayName(ride.getVehicleType() != null ? ride.getVehicleType().getDisplayName() : "");
        dto.setEstimatedFare(ride.getEstimatedFare());
        dto.setStatus(ride.getStatus());
        dto.setDriverName(ride.getDriverName());
        dto.setDriverVehicleNumber(ride.getDriverVehicleNumber());
        dto.setCreatedAt(ride.getCreatedAt());
        dto.setUpdatedAt(ride.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public Integer getRequestedSeats() {
        return requestedSeats;
    }

    public void setRequestedSeats(Integer requestedSeats) {
        this.requestedSeats = requestedSeats;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleDisplayName() {
        return vehicleDisplayName;
    }

    public void setVehicleDisplayName(String vehicleDisplayName) {
        this.vehicleDisplayName = vehicleDisplayName;
    }

    public BigDecimal getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(BigDecimal estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverVehicleNumber() {
        return driverVehicleNumber;
    }

    public void setDriverVehicleNumber(String driverVehicleNumber) {
        this.driverVehicleNumber = driverVehicleNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<RideAuditLogDTO> getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(List<RideAuditLogDTO> auditTrail) {
        this.auditTrail = auditTrail;
    }
}
