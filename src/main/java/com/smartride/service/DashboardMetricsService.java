package com.smartride.service;

import com.smartride.dto.DashboardMetricsDTO;
import com.smartride.model.Ride;
import com.smartride.model.RideStatus;
import com.smartride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardMetricsService {

    private final RideRepository rideRepository;

    @Autowired
    public DashboardMetricsService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public DashboardMetricsDTO calculateMetrics() {
        List<Ride> allRides = rideRepository.findAll();
        long totalRides = allRides.size();

        long requested = 0;
        long active = 0;
        long completed = 0;
        long cancelled = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        Map<String, Long> vehicleCounts = new HashMap<>();

        for (Ride ride : allRides) {
            RideStatus status = ride.getStatus();
            if (status == RideStatus.REQUESTED) {
                requested++;
            } else if (status == RideStatus.ACCEPTED || status == RideStatus.DRIVER_ASSIGNED || status == RideStatus.ONGOING) {
                active++;
            } else if (status == RideStatus.COMPLETED) {
                completed++;
                if (ride.getEstimatedFare() != null) {
                    totalRevenue = totalRevenue.add(ride.getEstimatedFare());
                }
            } else if (status == RideStatus.CANCELLED) {
                cancelled++;
            }

            if (ride.getVehicleType() != null) {
                String typeName = ride.getVehicleType().getDisplayName();
                vehicleCounts.put(typeName, vehicleCounts.getOrDefault(typeName, 0L) + 1);
            }
        }

        double utilizationRate = 0.0;
        if (totalRides > 0) {
            utilizationRate = ((double) (active + completed) / totalRides) * 100.0;
            utilizationRate = Math.round(utilizationRate * 10.0) / 10.0;
        }

        return new DashboardMetricsDTO(
            totalRides,
            requested,
            active,
            completed,
            cancelled,
            utilizationRate,
            totalRevenue.setScale(2, RoundingMode.HALF_UP),
            vehicleCounts
        );
    }
}
