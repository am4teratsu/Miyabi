package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.Reservation;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByGuest_IdGuest(Integer idGuest);
    
    // Buscar una reserva por su código exacto (Ej: RES-2026-0001)
    Reservation findByReservationCode(String reservationCode);
}