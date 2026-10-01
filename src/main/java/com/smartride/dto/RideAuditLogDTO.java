package com.smartride.dto;

import com.smartride.model.RideAuditLog;
import com.smartride.model.RideStatus;
import java.time.LocalDateTime;

public class RideAuditLogDTO {

    private Long id;
    private Long rideId;
    private RideStatus previousStatus;
    private RideStatus newStatus;
    private String actorRole;
    private String actorName;
    private String notes;
    private LocalDateTime timestamp;

    public RideAuditLogDTO() {
    }

    public static RideAuditLogDTO fromEntity(RideAuditLog entity) {
        RideAuditLogDTO dto = new RideAuditLogDTO();
        dto.setId(entity.getId());
        dto.setRideId(entity.getRideId());
        dto.setPreviousStatus(entity.getPreviousStatus());
        dto.setNewStatus(entity.getNewStatus());
        dto.setActorRole(entity.getActorRole());
        dto.setActorName(entity.getActorName());
        dto.setNotes(entity.getNotes());
        dto.setTimestamp(entity.getTimestamp());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public RideStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(RideStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public RideStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(RideStatus newStatus) {
        this.newStatus = newStatus;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
