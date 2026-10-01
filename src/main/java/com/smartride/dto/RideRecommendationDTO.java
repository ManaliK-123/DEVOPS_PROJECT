package com.smartride.dto;

import java.math.BigDecimal;

public class RideRecommendationDTO {

    private Long matchedRideId;
    private String passengerName;
    private String sourceLocation;
    private String destinationLocation;
    private String departureTimeFormatted;
    private String vehicleDisplayName;
    private BigDecimal estimatedFare;
    private BigDecimal potentialSavings;
    private int matchScorePercent;
    private String matchReason;

    public RideRecommendationDTO() {
    }

    public Long getMatchedRideId() {
        return matchedRideId;
    }

    public void setMatchedRideId(Long matchedRideId) {
        this.matchedRideId = matchedRideId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
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

    public String getDepartureTimeFormatted() {
        return departureTimeFormatted;
    }

    public void setDepartureTimeFormatted(String departureTimeFormatted) {
        this.departureTimeFormatted = departureTimeFormatted;
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

    public BigDecimal getPotentialSavings() {
        return potentialSavings;
    }

    public void setPotentialSavings(BigDecimal potentialSavings) {
        this.potentialSavings = potentialSavings;
    }

    public int getMatchScorePercent() {
        return matchScorePercent;
    }

    public void setMatchScorePercent(int matchScorePercent) {
        this.matchScorePercent = matchScorePercent;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }
}
