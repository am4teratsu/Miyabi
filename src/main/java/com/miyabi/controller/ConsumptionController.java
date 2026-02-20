package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.miyabi.models.Consumption;
import com.miyabi.service.ConsumptionService;

@RestController
@RequestMapping("/api/consumptions")
public class ConsumptionController {

    private final ConsumptionService consumptionService;

    public ConsumptionController(ConsumptionService consumptionService) {
        this.consumptionService = consumptionService;
    }

    @GetMapping
    public List<Consumption> getAllConsumptions() {
        return consumptionService.findAll();
    }

    @GetMapping("/reservation/{reservationId}")
    public List<Consumption> getConsumptionsByReservation(@PathVariable Integer reservationId) {
        return consumptionService.findByReservationId(reservationId);
    }

    @PostMapping
    public Consumption createConsumption(@RequestBody Consumption consumption) {
        return consumptionService.save(consumption);
    }
}