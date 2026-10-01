package com.smartride.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ride_audit_logs")
public class RideAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long rideId;

    @Enumerated(EnumType.STRING)
    private RideStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus newStatus;

    @Column(nullable = false)
    private String actorRole;

    @Column(nullable = false)
    private String actorName;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public RideAuditLog() {
    }

    public RideAuditLog(Long rideId, RideStatus previousStatus, RideStatus newStatus,
                        String actorRole, String actorName, String notes) {
        this.rideId = rideId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.actorRole = actorRole;
        this.actorName = actorName;
        this.notes = notes;
        this.timestamp = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
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
