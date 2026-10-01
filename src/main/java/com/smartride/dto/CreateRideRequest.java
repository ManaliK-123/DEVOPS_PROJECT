package com.smartride.dto;

import com.smartride.model.VehicleType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateRideRequest {

    @NotBlank(message = "Passenger name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String passengerName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+()\\-\\s]{7,20}$", message = "Invalid phone number format")
    private String passengerPhone;

    @NotBlank(message = "Pickup source location is required")
    private String sourceLocation;

    @NotBlank(message = "Drop-off destination is required")
    private String destinationLocation;

    @NotNull(message = "Departure time is required")
    @FutureOrPresent(message = "Departure time cannot be in the past")
    private LocalDateTime departureTime;

    @NotNull(message = "Requested seats is required")
    @Min(value = 1, message = "Must request at least 1 seat")
    @Max(value = 6, message = "Maximum 6 seats per request")
    private Integer requestedSeats;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    private BigDecimal customFare;

    public CreateRideRequest() {
    }

    public CreateRideRequest(String passengerName, String passengerPhone, String sourceLocation,
                             String destinationLocation, LocalDateTime departureTime,
                             Integer requestedSeats, VehicleType vehicleType, BigDecimal customFare) {
        this.passengerName = passengerName;
        this.passengerPhone = passengerPhone;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.departureTime = departureTime;
        this.requestedSeats = requestedSeats;
        this.vehicleType = vehicleType;
        this.customFare = customFare;
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

    public BigDecimal getCustomFare() {
        return customFare;
    }

    public void setCustomFare(BigDecimal customFare) {
        this.customFare = customFare;
    }
}
