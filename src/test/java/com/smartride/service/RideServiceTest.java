package com.smartride.service;

import com.smartride.dto.CreateRideRequest;
import com.smartride.dto.RideResponseDTO;
import com.smartride.dto.UpdateStatusRequest;
import com.smartride.exception.InvalidStateTransitionException;
import com.smartride.model.RideStatus;
import com.smartride.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RideServiceTest {

    @Autowired
    private RideService rideService;

    @Autowired
    private DataSeederService dataSeederService;

    @BeforeEach
    void setUp() {
        rideService.resetToSeedData();
        dataSeederService.seedInitialData();
    }

    @Test
    @DisplayName("Should create a new ride request and record audit log")
    void testCreateRide() {
        CreateRideRequest request = new CreateRideRequest(
            "Sophia Martinez",
            "+1-555-4321",
            "South Bay Marina",
            "Tech Hub Park",
            LocalDateTime.now().plusHours(1),
            2,
            VehicleType.SEDAN,
            new BigDecimal("28.50")
        );

        RideResponseDTO created = rideService.createRide(request);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getPassengerName()).isEqualTo("Sophia Martinez");
        assertThat(created.getStatus()).isEqualTo(RideStatus.REQUESTED);
        assertThat(created.getAuditTrail()).isNotEmpty();
        assertThat(created.getAuditTrail().get(0).getNewStatus()).isEqualTo(RideStatus.REQUESTED);
    }

    @Test
    @DisplayName("Should transition status through valid lifecycle: REQUESTED -> ACCEPTED -> DRIVER_ASSIGNED -> ONGOING -> COMPLETED")
    void testValidStatusLifecycle() {
        // Create initial
        CreateRideRequest request = new CreateRideRequest(
            "David Kim", "+1-555-8888", "Zone A", "Zone B",
            LocalDateTime.now().plusMinutes(20), 1, VehicleType.SEDAN, new BigDecimal("20.00")
        );
        RideResponseDTO ride = rideService.createRide(request);
        Long id = ride.getId();

        // 1. Accept
        UpdateStatusRequest acceptReq = new UpdateStatusRequest(
            RideStatus.ACCEPTED, "DRIVER", "David Miller", "David Miller", "EV-9021-X", "Driver accepted"
        );
        RideResponseDTO accepted = rideService.updateRideStatus(id, acceptReq);
        assertThat(accepted.getStatus()).isEqualTo(RideStatus.ACCEPTED);
        assertThat(accepted.getDriverName()).isEqualTo("David Miller");

        // 2. Driver Assigned
        UpdateStatusRequest assignReq = new UpdateStatusRequest(
            RideStatus.DRIVER_ASSIGNED, "DRIVER", "David Miller", "David Miller", "EV-9021-X", "Arrived at pickup"
        );
        RideResponseDTO assigned = rideService.updateRideStatus(id, assignReq);
        assertThat(assigned.getStatus()).isEqualTo(RideStatus.DRIVER_ASSIGNED);

        // 3. Ongoing
        UpdateStatusRequest startReq = new UpdateStatusRequest(
            RideStatus.ONGOING, "DRIVER", "David Miller", "David Miller", "EV-9021-X", "Trip ongoing"
        );
        RideResponseDTO ongoing = rideService.updateRideStatus(id, startReq);
        assertThat(ongoing.getStatus()).isEqualTo(RideStatus.ONGOING);

        // 4. Completed
        UpdateStatusRequest completeReq = new UpdateStatusRequest(
            RideStatus.COMPLETED, "DRIVER", "David Miller", "David Miller", "EV-9021-X", "Trip completed"
        );
        RideResponseDTO completed = rideService.updateRideStatus(id, completeReq);
        assertThat(completed.getStatus()).isEqualTo(RideStatus.COMPLETED);
        assertThat(completed.getAuditTrail().size()).isGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("Should reject invalid state transition (e.g. REQUESTED -> COMPLETED)")
    void testInvalidStatusTransition() {
        CreateRideRequest request = new CreateRideRequest(
            "Test Passenger", "+1-555-9999", "Location A", "Location B",
            LocalDateTime.now().plusMinutes(10), 1, VehicleType.HATCHBACK, new BigDecimal("15.00")
        );
        RideResponseDTO ride = rideService.createRide(request);

        UpdateStatusRequest invalidReq = new UpdateStatusRequest(
            RideStatus.COMPLETED, "DRIVER", "Driver 1", "Driver 1", "SD-100", "Attempt invalid jump"
        );

        assertThrows(InvalidStateTransitionException.class, () -> {
            rideService.updateRideStatus(ride.getId(), invalidReq);
        });
    }

    @Test
    @DisplayName("Should calculate deterministic estimated fare")
    void testCalculateEstimatedFare() {
        BigDecimal fareSedan = rideService.calculateEstimatedFare("Airport", "Downtown", 1, "SEDAN");
        BigDecimal fareSuv = rideService.calculateEstimatedFare("Airport", "Downtown", 1, "SUV");

        assertThat(fareSedan).isGreaterThan(BigDecimal.ZERO);
        assertThat(fareSuv).isGreaterThan(fareSedan);
    }

    @Test
    @DisplayName("Should search rides by passenger name or location keyword")
    void testSearchRides() {
        List<RideResponseDTO> results = rideService.searchRides("Airport");
        assertThat(results).isNotEmpty();
    }
}
