package com.smartride.model;

public enum RideStatus {
    REQUESTED,
    ACCEPTED,
    DRIVER_ASSIGNED,
    ONGOING,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(RideStatus target) {
        if (this == target) return true;
        return switch (this) {
            case REQUESTED -> target == ACCEPTED || target == CANCELLED;
            case ACCEPTED -> target == DRIVER_ASSIGNED || target == CANCELLED;
            case DRIVER_ASSIGNED -> target == ONGOING || target == CANCELLED;
            case ONGOING -> target == COMPLETED || target == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
