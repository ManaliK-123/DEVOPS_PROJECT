package com.smartride.controller;

import com.smartride.model.VehicleType;
import com.smartride.service.DashboardMetricsService;
import com.smartride.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RideWebController {

    private final RideService rideService;
    private final DashboardMetricsService metricsService;

    @Autowired
    public RideWebController(RideService rideService, DashboardMetricsService metricsService) {
        this.rideService = rideService;
        this.metricsService = metricsService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("vehicleTypes", VehicleType.values());
        model.addAttribute("metrics", metricsService.calculateMetrics());
        model.addAttribute("rides", rideService.getAllRides());
        return "index";
    }

    @GetMapping("/passenger")
    public String passengerPortal(Model model) {
        model.addAttribute("vehicleTypes", VehicleType.values());
        model.addAttribute("rides", rideService.getAllRides());
        return "index";
    }

    @GetMapping("/driver")
    public String driverPortal(Model model) {
        model.addAttribute("rides", rideService.getAllRides());
        return "index";
    }

    @GetMapping("/admin")
    public String adminPortal(Model model) {
        model.addAttribute("metrics", metricsService.calculateMetrics());
        model.addAttribute("rides", rideService.getAllRides());
        return "index";
    }
}
