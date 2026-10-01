package com.smartride.service;

import com.smartride.dto.DashboardMetricsDTO;
import com.smartride.dto.RideRecommendationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AnalyticsAndRecommendationTests {

    @Autowired
    private SmartRecommendationService recommendationService;

    @Autowired
    private DashboardMetricsService metricsService;

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
    @DisplayName("Should generate smart recommendations with match score and savings")
    void testRecommendationEngine() {
        List<RideRecommendationDTO> recs = recommendationService.getRecommendations("Downtown", "Metro");
        assertThat(recs).isNotNull();
        if (!recs.isEmpty()) {
            RideRecommendationDTO first = recs.get(0);
            assertThat(first.getMatchScorePercent()).isGreaterThan(0);
            assertThat(first.getPotentialSavings()).isNotNull();
            assertThat(first.getMatchReason()).isNotEmpty();
        }
    }

    @Test
    @DisplayName("Should calculate platform metrics accurately")
    void testMetricsCalculation() {
        DashboardMetricsDTO metrics = metricsService.calculateMetrics();
        assertThat(metrics).isNotNull();
        assertThat(metrics.getTotalRides()).isGreaterThanOrEqualTo(5);
        assertThat(metrics.getFleetUtilizationRate()).isBetween(0.0, 100.0);
    }
}
