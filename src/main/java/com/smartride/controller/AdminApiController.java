package com.smartride.controller;

import com.smartride.dto.DashboardMetricsDTO;
import com.smartride.service.DashboardMetricsService;
import com.smartride.service.DataSeederService;
import com.smartride.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminApiController {

    private final DashboardMetricsService metricsService;
    private final RideService rideService;
    private final DataSeederService dataSeederService;

    @Autowired
    public AdminApiController(DashboardMetricsService metricsService, RideService rideService, DataSeederService dataSeederService) {
        this.metricsService = metricsService;
        this.rideService = rideService;
        this.dataSeederService = dataSeederService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDTO> getMetrics() {
        return ResponseEntity.ok(metricsService.calculateMetrics());
    }

    @PostMapping("/reset-seed")
    public ResponseEntity<Map<String, Object>> resetSeedData() {
        rideService.resetToSeedData();
        dataSeederService.seedInitialData();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "Database successfully reset to seed demo state");
        return ResponseEntity.ok(resp);
    }
}
