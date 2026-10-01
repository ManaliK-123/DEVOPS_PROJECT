package com.smartride.controller;

import com.smartride.dto.CreateRideRequest;
import com.smartride.dto.RideRecommendationDTO;
import com.smartride.dto.RideResponseDTO;
import com.smartride.dto.UpdateStatusRequest;
import com.smartride.model.RideStatus;
import com.smartride.service.RideService;
import com.smartride.service.SmartRecommendationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin(origins = "*")
public class RideApiController {

    private final RideService rideService;
    private final SmartRecommendationService recommendationService;

    @Autowired
    public RideApiController(RideService rideService, SmartRecommendationService recommendationService) {
        this.rideService = rideService;
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<List<RideResponseDTO>> getRides(
            @RequestParam(required = false) RideStatus status,
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(rideService.searchRides(search));
        } else if (status != null) {
            return ResponseEntity.ok(rideService.getRidesByStatus(status));
        }
        return ResponseEntity.ok(rideService.getAllRides());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponseDTO> getRideById(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.getRideById(id));
    }

    @PostMapping
    public ResponseEntity<RideResponseDTO> createRide(@Valid @RequestBody CreateRideRequest request) {
        RideResponseDTO created = rideService.createRide(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RideResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(rideService.updateRideStatus(id, request));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<RideRecommendationDTO>> getRecommendations(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination) {
        return ResponseEntity.ok(recommendationService.getRecommendations(source, destination));
    }

    @GetMapping("/estimate-fare")
    public ResponseEntity<Map<String, Object>> estimateFare(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(defaultValue = "1") Integer seats,
            @RequestParam(defaultValue = "SEDAN") String vehicleType) {
        BigDecimal fare = rideService.calculateEstimatedFare(source, destination, seats, vehicleType);
        Map<String, Object> resp = new HashMap<>();
        resp.put("source", source);
        resp.put("destination", destination);
        resp.put("seats", seats);
        resp.put("vehicleType", vehicleType);
        resp.put("estimatedFare", fare);
        return ResponseEntity.ok(resp);
    }
}
