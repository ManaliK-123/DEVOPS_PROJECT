package com.smartride.dto;

import com.smartride.model.RideStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {

    @NotNull(message = "Target status is required")
    private RideStatus targetStatus;

    @NotBlank(message = "Actor role is required (PASSENGER, DRIVER, ADMIN)")
    private String actorRole;

    private String actorName;

    private String driverName;

    private String driverVehicleNumber;

    private String notes;

    public UpdateStatusRequest() {
    }

    public UpdateStatusRequest(RideStatus targetStatus, String actorRole, String actorName,
                               String driverName, String driverVehicleNumber, String notes) {
        this.targetStatus = targetStatus;
        this.actorRole = actorRole;
        this.actorName = actorName;
        this.driverName = driverName;
        this.driverVehicleNumber = driverVehicleNumber;
        this.notes = notes;
    }

    public RideStatus getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(RideStatus targetStatus) {
        this.targetStatus = targetStatus;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
