package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.miyabi.models.Reservation;
import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz de Repositorio para la entidad Reservation.
 * Es el repositorio con mayor carga de lógica de datos, ya que alimenta tanto
 * el portal del cliente como el Dashboard administrativo del hotel.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
    /**
     * Obtiene el historial de reservas de un cliente específico.
     * Utilizado para poblar la vista "Mis Reservas" en el portal del huésped.
     */
    List<Reservation> findByGuest_IdGuest(Integer idGuest);

    /**
     * Busca una reserva por su código público (Ej. RES-2026-X).
     * Vital para la búsqueda rápida en recepción o validación de vouchers.
     */
    Reservation findByReservationCode(String reservationCode);

    /**
     * Consulta JPQL para el Dashboard de Finanzas.
     * Suma todos los ingresos generados por reservas, excluyendo aquellas que fueron canceladas.
     * @return El monto total recaudado por el hotel.
     */
    @Query("SELECT SUM(r.totalPay) FROM Reservation r WHERE r.state != 'Cancelled'")
    BigDecimal sumTotalRevenue();

    /**
     * Reporte Estadístico: Cuenta cuántas reservas hay en un estado específico.
     * (Ej. Cuántas hay "Pending", "Confirmed" o "Completed").
     */
    long countByState(String state);
    
    /**
     * Obtiene las últimas 5 reservas registradas en el sistema.
     * Ideal para la tabla de "Movimientos Recientes" en el inicio del panel administrativo.
     */
    List<Reservation> findTop5ByOrderByReservationIdDesc();

    /**
     * Consulta de Optimización para la Boleta (Receipt).
     * 'JOIN FETCH' obliga a JPA a traer los datos del Huésped y la Habitación en una sola
     * consulta SQL (Eager Loading), evitando el problema de rendimiento "N+1 select".
     * @param reservationId ID de la reserva a facturar.
     * @return Objeto Reservation con sus relaciones cargadas.
     */
    @Query("SELECT r FROM Reservation r JOIN FETCH r.guest JOIN FETCH r.room WHERE r.reservationId = :reservationId")
    Reservation getReceiptMainData(@Param("reservationId") Integer reservationId);
}