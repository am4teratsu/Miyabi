package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.miyabi.models.Reservation;
import java.math.BigDecimal;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
    List<Reservation> findByGuest_IdGuest(Integer idGuest);
    Reservation findByReservationCode(String reservationCode);

    // Sumar todo el dinero de reservas que no estén canceladas
    @Query("SELECT SUM(r.totalPay) FROM Reservation r WHERE r.state != 'Cancelled'")
    BigDecimal sumTotalRevenue();

    // Contar reservas por estado
    long countByState(String state);
    
    // Obtener las últimas 5 reservas para la tabla de movimientos
    List<Reservation> findTop5ByOrderByReservationIdDesc();


    @Query("SELECT r FROM Reservation r JOIN FETCH r.guest JOIN FETCH r.room WHERE r.reservationId = :reservationId")
    Reservation getReceiptMainData(@Param("reservationId") Integer reservationId);
}