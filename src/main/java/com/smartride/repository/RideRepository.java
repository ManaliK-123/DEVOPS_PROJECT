package com.smartride.repository;

import com.smartride.model.Ride;
import com.smartride.model.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByStatusOrderByCreatedAtDesc(RideStatus status);

    List<Ride> findAllByOrderByCreatedAtDesc();

    long countByStatus(RideStatus status);

    @Query("SELECT r FROM Ride r WHERE LOWER(r.sourceLocation) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(r.destinationLocation) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(r.passengerName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY r.createdAt DESC")
    List<Ride> searchRides(String query);

    @Query("SELECT r FROM Ride r WHERE r.status = 'REQUESTED' " +
           "AND LOWER(r.sourceLocation) = LOWER(:source) " +
           "AND LOWER(r.destinationLocation) = LOWER(:destination) " +
           "ORDER BY r.departureTime ASC")
    List<Ride> findExactRouteMatches(String source, String destination);

    @Query("SELECT r FROM Ride r WHERE r.status = 'REQUESTED' " +
           "AND (LOWER(r.sourceLocation) LIKE LOWER(CONCAT('%', :sourceKeyword, '%')) " +
           "OR LOWER(r.destinationLocation) LIKE LOWER(CONCAT('%', :destKeyword, '%'))) " +
           "ORDER BY r.departureTime ASC")
    List<Ride> findFuzzyMatches(String sourceKeyword, String destKeyword);
}
