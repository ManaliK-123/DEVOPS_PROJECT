package com.smartride.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartride.dto.CreateRideRequest;
import com.smartride.dto.UpdateStatusRequest;
import com.smartride.model.RideStatus;
import com.smartride.model.VehicleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RideApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/rides returns list of rides")
    void testGetAllRides() throws Exception {
        mockMvc.perform(get("/api/rides"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("POST /api/rides creates a new ride")
    void testCreateRideApi() throws Exception {
        CreateRideRequest request = new CreateRideRequest(
            "REST Test User",
            "+1-555-7777",
            "Point Alpha",
            "Point Beta",
            LocalDateTime.now().plusHours(2),
            1,
            VehicleType.SEDAN,
            new BigDecimal("25.00")
        );

        mockMvc.perform(post("/api/rides")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.passengerName", is("REST Test User")))
            .andExpect(jsonPath("$.status", is("REQUESTED")));
    }

    @Test
    @DisplayName("GET /api/rides/recommendations returns list of recommendations")
    void testGetRecommendations() throws Exception {
        mockMvc.perform(get("/api/rides/recommendations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("GET /api/admin/metrics returns aggregate analytics")
    void testGetMetrics() throws Exception {
        mockMvc.perform(get("/api/admin/metrics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalRides", greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.fleetUtilizationRate", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/health returns HEALTHY status")
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("HEALTHY")));
    }
}
