package com.smartride.service;

import com.smartride.dto.CreateRideRequest;
import com.smartride.dto.RideResponseDTO;
import com.smartride.dto.UpdateStatusRequest;
import com.smartride.model.RideStatus;

import java.math.BigDecimal;
import java.util.List;

public interface RideService {

    RideResponseDTO createRide(CreateRideRequest request);

    RideResponseDTO getRideById(Long id);

    List<RideResponseDTO> getAllRides();

    List<RideResponseDTO> getRidesByStatus(RideStatus status);

    List<RideResponseDTO> searchRides(String query);

    RideResponseDTO updateRideStatus(Long id, UpdateStatusRequest request);

    BigDecimal calculateEstimatedFare(String source, String destination, Integer seats, String vehicleTypeStr);

    void resetToSeedData();
}
