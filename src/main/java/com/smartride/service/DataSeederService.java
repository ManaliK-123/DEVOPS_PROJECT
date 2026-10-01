package com.smartride.service;

import com.smartride.model.Ride;
import com.smartride.model.RideAuditLog;
import com.smartride.model.RideStatus;
import com.smartride.model.VehicleType;
import com.smartride.repository.RideAuditLogRepository;
import com.smartride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class DataSeederService implements CommandLineRunner {

    private final RideRepository rideRepository;
    private final RideAuditLogRepository auditLogRepository;

    @Autowired
    public DataSeederService(RideRepository rideRepository, RideAuditLogRepository auditLogRepository) {
        this.rideRepository = rideRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void run(String... args) {
        if (rideRepository.count() == 0) {
            seedInitialData();
        }
    }

    public void seedInitialData() {
        LocalDateTime now = LocalDateTime.now();

        // 1. REQUESTED Ride
        Ride r1 = new Ride("Sarah Jenkins", "+1-555-0101", "Downtown Financial District", "Metro Tech Park",
            now.plusMinutes(25), 2, VehicleType.SEDAN, new BigDecimal("34.50"));
        r1.setStatus(RideStatus.REQUESTED);
        Ride savedR1 = rideRepository.save(r1);
        auditLogRepository.save(new RideAuditLog(savedR1.getId(), null, RideStatus.REQUESTED, "PASSENGER", "Sarah Jenkins", "Initial ride requested"));

        // 2. ACCEPTED Ride
        Ride r2 = new Ride("Alex Mercer", "+1-555-0102", "International Airport Terminal 2", "Grand Central Station",
            now.plusMinutes(10), 1, VehicleType.ELECTRIC_PREMIUM, new BigDecimal("48.75"));
        r2.setStatus(RideStatus.ACCEPTED);
        r2.setDriverName("David Miller");
        r2.setDriverVehicleNumber("EV-9021-X");
        Ride savedR2 = rideRepository.save(r2);
        auditLogRepository.save(new RideAuditLog(savedR2.getId(), null, RideStatus.REQUESTED, "PASSENGER", "Alex Mercer", "Airport ride requested"));
        auditLogRepository.save(new RideAuditLog(savedR2.getId(), RideStatus.REQUESTED, RideStatus.ACCEPTED, "DRIVER", "David Miller", "Driver accepted airport run"));

        // 3. DRIVER_ASSIGNED Ride
        Ride r3 = new Ride("Elena Rostova", "+1-555-0103", "Westside Medical Center", "Northside Suburbs",
            now.plusMinutes(5), 3, VehicleType.SUV, new BigDecimal("55.00"));
        r3.setStatus(RideStatus.DRIVER_ASSIGNED);
        r3.setDriverName("Marcus Vance");
        r3.setDriverVehicleNumber("SUV-8820-T");
        Ride savedR3 = rideRepository.save(r3);
        auditLogRepository.save(new RideAuditLog(savedR3.getId(), null, RideStatus.REQUESTED, "PASSENGER", "Elena Rostova", "Medical transit booking"));
        auditLogRepository.save(new RideAuditLog(savedR3.getId(), RideStatus.REQUESTED, RideStatus.ACCEPTED, "DRIVER", "Marcus Vance", "Driver accepted request"));
        auditLogRepository.save(new RideAuditLog(savedR3.getId(), RideStatus.ACCEPTED, RideStatus.DRIVER_ASSIGNED, "DRIVER", "Marcus Vance", "Driver arrived at pickup gate"));

        // 4. ONGOING Ride
        Ride r4 = new Ride("Rajesh Patel", "+1-555-0104", "East Bay Marina", "Silicon Innovation Campus",
            now.minusMinutes(15), 1, VehicleType.HATCHBACK, new BigDecimal("22.80"));
        r4.setStatus(RideStatus.ONGOING);
        r4.setDriverName("Carlos Santos");
        r4.setDriverVehicleNumber("HB-3301-C");
        Ride savedR4 = rideRepository.save(r4);
        auditLogRepository.save(new RideAuditLog(savedR4.getId(), null, RideStatus.REQUESTED, "PASSENGER", "Rajesh Patel", "Morning commute booked"));
        auditLogRepository.save(new RideAuditLog(savedR4.getId(), RideStatus.REQUESTED, RideStatus.ACCEPTED, "DRIVER", "Carlos Santos", "Carlos accepted"));
        auditLogRepository.save(new RideAuditLog(savedR4.getId(), RideStatus.ACCEPTED, RideStatus.DRIVER_ASSIGNED, "DRIVER", "Carlos Santos", "Driver at pickup"));
        auditLogRepository.save(new RideAuditLog(savedR4.getId(), RideStatus.DRIVER_ASSIGNED, RideStatus.ONGOING, "DRIVER", "Carlos Santos", "Trip started heading towards Campus"));

        // 5. COMPLETED Ride
        Ride r5 = new Ride("Michael Chang", "+1-555-0105", "University Campus Gate 4", "City Center Mall",
            now.minusHours(2), 2, VehicleType.SEDAN, new BigDecimal("18.50"));
        r5.setStatus(RideStatus.COMPLETED);
        r5.setDriverName("Sarah Connor");
        r5.setDriverVehicleNumber("SD-1104-M");
        Ride savedR5 = rideRepository.save(r5);
        auditLogRepository.save(new RideAuditLog(savedR5.getId(), null, RideStatus.REQUESTED, "PASSENGER", "Michael Chang", "Mall visit booked"));
        auditLogRepository.save(new RideAuditLog(savedR5.getId(), RideStatus.REQUESTED, RideStatus.ACCEPTED, "DRIVER", "Sarah Connor", "Accepted"));
        auditLogRepository.save(new RideAuditLog(savedR5.getId(), RideStatus.ACCEPTED, RideStatus.DRIVER_ASSIGNED, "DRIVER", "Sarah Connor", "Assigned"));
        auditLogRepository.save(new RideAuditLog(savedR5.getId(), RideStatus.DRIVER_ASSIGNED, RideStatus.ONGOING, "DRIVER", "Sarah Connor", "In transit"));
        auditLogRepository.save(new RideAuditLog(savedR5.getId(), RideStatus.ONGOING, RideStatus.COMPLETED, "DRIVER", "Sarah Connor", "Safely arrived at destination"));

        // 6. CANCELLED Ride
        Ride r6 = new Ride("Chloe Bennett", "+1-555-0106", "Convention Center Hall B", "Sunset Hills Hotel",
            now.minusHours(3), 1, VehicleType.ELECTRIC_PREMIUM, new BigDecimal("29.00"));
        r6.setStatus(RideStatus.CANCELLED);
        Ride savedR6 = rideRepository.save(r6);
        auditLogRepository.save(new RideAuditLog(savedR6.getId(), null, RideStatus.REQUESTED, "PASSENGER", "Chloe Bennett", "Hotel transfer requested"));
        auditLogRepository.save(new RideAuditLog(savedR6.getId(), RideStatus.REQUESTED, RideStatus.CANCELLED, "PASSENGER", "Chloe Bennett", "Passenger cancelled due to meeting delay"));
    }
}
