package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.Consumption;
import java.util.List;

public interface ConsumptionRepository extends JpaRepository<Consumption, Integer> {
    List<Consumption> findByReservation_ReservationId(Integer reservationId);
}