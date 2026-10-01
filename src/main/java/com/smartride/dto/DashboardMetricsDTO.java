package com.smartride.dto;

import java.math.BigDecimal;
import java.util.Map;

public class DashboardMetricsDTO {

    private long totalRides;
    private long requestedRides;
    private long activeRides;
    private long completedRides;
    private long cancelledRides;
    private double fleetUtilizationRate;
    private BigDecimal totalRevenue;
    private Map<String, Long> vehicleDistribution;

    public DashboardMetricsDTO() {
    }

    public DashboardMetricsDTO(long totalRides, long requestedRides, long activeRides,
                               long completedRides, long cancelledRides, double fleetUtilizationRate,
                               BigDecimal totalRevenue, Map<String, Long> vehicleDistribution) {
        this.totalRides = totalRides;
        this.requestedRides = requestedRides;
        this.activeRides = activeRides;
        this.completedRides = completedRides;
        this.cancelledRides = cancelledRides;
        this.fleetUtilizationRate = fleetUtilizationRate;
        this.totalRevenue = totalRevenue;
        this.vehicleDistribution = vehicleDistribution;
    }

    public long getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(long totalRides) {
        this.totalRides = totalRides;
    }

    public long getRequestedRides() {
        return requestedRides;
    }

    public void setRequestedRides(long requestedRides) {
        this.requestedRides = requestedRides;
    }

    public long getActiveRides() {
        return activeRides;
    }

    public void setActiveRides(long activeRides) {
        this.activeRides = activeRides;
    }

    public long getCompletedRides() {
        return completedRides;
    }

    public void setCompletedRides(long completedRides) {
        this.completedRides = completedRides;
    }

    public long getCancelledRides() {
        return cancelledRides;
    }

    public void setCancelledRides(long cancelledRides) {
        this.cancelledRides = cancelledRides;
    }

    public double getFleetUtilizationRate() {
        return fleetUtilizationRate;
    }

    public void setFleetUtilizationRate(double fleetUtilizationRate) {
        this.fleetUtilizationRate = fleetUtilizationRate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Map<String, Long> getVehicleDistribution() {
        return vehicleDistribution;
    }

    public void setVehicleDistribution(Map<String, Long> vehicleDistribution) {
        this.vehicleDistribution = vehicleDistribution;
    }
}
