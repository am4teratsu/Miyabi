package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.Payments;

public interface PaymentsRepository extends JpaRepository<Payments, Integer> {
    Payments findByReservation_ReservationId(Integer reservationId);
}