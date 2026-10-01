package com.smartride.model;

import java.math.BigDecimal;

public enum VehicleType {
    SEDAN("Sedan", 4, new BigDecimal("1.0")),
    SUV("SUV Premium", 6, new BigDecimal("1.5")),
    HATCHBACK("Compact Hatchback", 4, new BigDecimal("0.85")),
    ELECTRIC_PREMIUM("Electric Eco-Drive", 4, new BigDecimal("1.3"));

    private final String displayName;
    private final int maxSeats;
    private final BigDecimal fareMultiplier;

    VehicleType(String displayName, int maxSeats, BigDecimal fareMultiplier) {
        this.displayName = displayName;
        this.maxSeats = maxSeats;
        this.fareMultiplier = fareMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public BigDecimal getFareMultiplier() {
        return fareMultiplier;
    }
}
