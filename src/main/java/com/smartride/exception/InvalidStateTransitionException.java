package com.smartride.exception;

import com.smartride.model.RideStatus;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(RideStatus from, RideStatus to) {
        super("Invalid status transition: Cannot change ride status from " + from + " to " + to);
    }

    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
