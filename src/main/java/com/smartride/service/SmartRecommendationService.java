package com.smartride.service;

import com.smartride.dto.RideRecommendationDTO;
import com.smartride.model.Ride;
import com.smartride.model.RideStatus;
import com.smartride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SmartRecommendationService {

    private final RideRepository rideRepository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    @Autowired
    public SmartRecommendationService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public List<RideRecommendationDTO> getRecommendations(String source, String destination) {
        List<RideRecommendationDTO> recommendations = new ArrayList<>();
        List<Ride> candidateRides = rideRepository.findByStatusOrderByCreatedAtDesc(RideStatus.REQUESTED);

        String srcClean = (source != null) ? source.trim().toLowerCase() : "";
        String dstClean = (destination != null) ? destination.trim().toLowerCase() : "";

        for (Ride ride : candidateRides) {
            String rideSrc = ride.getSourceLocation().toLowerCase();
            String rideDst = ride.getDestinationLocation().toLowerCase();

            int matchScore = 0;
            String reason = "";

            if (!srcClean.isEmpty() && !dstClean.isEmpty()) {
                if (rideSrc.contains(srcClean) && rideDst.contains(dstClean)) {
                    matchScore = 95;
                    reason = "Exact matching corridor & compatible schedule";
                } else if (rideSrc.contains(srcClean) || rideDst.contains(dstClean)) {
                    matchScore = 78;
                    reason = "Partial corridor overlap with adjacent drop-off zone";
                } else {
                    matchScore = 60;
                    reason = "High driver density zone matching departure window";
                }
            } else {
                matchScore = 85;
                reason = "Popular commuter corridor with high driver acceptance rate";
            }

            BigDecimal savings = ride.getEstimatedFare().multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);

            RideRecommendationDTO rec = new RideRecommendationDTO();
            rec.setMatchedRideId(ride.getId());
            rec.setPassengerName(ride.getPassengerName());
            rec.setSourceLocation(ride.getSourceLocation());
            rec.setDestinationLocation(ride.getDestinationLocation());
            rec.setDepartureTimeFormatted(ride.getDepartureTime().format(TIME_FORMATTER));
            rec.setVehicleDisplayName(ride.getVehicleType().getDisplayName());
            rec.setEstimatedFare(ride.getEstimatedFare());
            rec.setPotentialSavings(savings);
            rec.setMatchScorePercent(matchScore);
            rec.setMatchReason(reason);

            recommendations.add(rec);
            if (recommendations.size() >= 5) break;
        }

        return recommendations;
    }
}
