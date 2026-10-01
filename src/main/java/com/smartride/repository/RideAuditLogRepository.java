package com.smartride.repository;

import com.smartride.model.RideAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideAuditLogRepository extends JpaRepository<RideAuditLog, Long> {

    List<RideAuditLog> findByRideIdOrderByTimestampAsc(Long rideId);

    List<RideAuditLog> findTop20ByOrderByTimestampDesc();
}
