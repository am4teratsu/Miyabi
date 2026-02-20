package com.miyabi.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

import com.miyabi.models.Consumption;
import com.miyabi.repository.ConsumptionRepository;

@Service
public class ConsumptionService {

    private final ConsumptionRepository consumptionRepository;

    public ConsumptionService(ConsumptionRepository consumptionRepository) {
        this.consumptionRepository = consumptionRepository;
    }

    public List<Consumption> findAll() {
        return consumptionRepository.findAll();
    }

    public Consumption findById(Integer id) {
        return consumptionRepository.findById(id).orElse(null);
    }

    public List<Consumption> findByReservationId(Integer reservationId) {
        return consumptionRepository.findByReservation_ReservationId(reservationId);
    }

    public Consumption save(Consumption consumption) {
        
        if (consumption.getAmount() != null && consumption.getUnitPrice() != null) {
            BigDecimal amountStr = new BigDecimal(consumption.getAmount());
            BigDecimal calculatedSubtotal = consumption.getUnitPrice().multiply(amountStr);
            consumption.setSubtotal(calculatedSubtotal);
        }
        
        return consumptionRepository.save(consumption);
    }
}