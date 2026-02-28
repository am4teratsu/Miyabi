package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.miyabi.models.Consumption;
import java.util.List;

public interface ConsumptionRepository extends JpaRepository<Consumption, Integer> {
    
    List<Consumption> findByReservation_ReservationId(Integer reservationId);

    @Query(value = "SELECT c.amount, s.service_name, c.unit_price, c.subtotal " +
                   "FROM consumption c " +
                   "INNER JOIN services_catalog s ON c.service_id = s.service_id " +
                   "WHERE c.reservation_id = :reservationId", nativeQuery = true)
    List<Object[]> getReceiptDetails(@Param("reservationId") Integer reservationId);
}