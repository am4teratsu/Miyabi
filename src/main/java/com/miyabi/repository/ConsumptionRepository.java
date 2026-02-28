package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.miyabi.models.Consumption;
import java.util.List;

/**
 * Interfaz de Repositorio para la entidad Consumption.
 * Maneja el acceso a datos de los consumos adicionales realizados por los huéspedes.
 */
public interface ConsumptionRepository extends JpaRepository<Consumption, Integer> {
    
    /**
     * Query Method: Spring Data JPA interpreta el nombre del método para generar la consulta.
     * Busca todos los consumos asociados a un ID de reserva específico.
     * @param reservationId ID de la reserva.
     * @return Lista de objetos Consumption.
     */
    List<Consumption> findByReservation_ReservationId(Integer reservationId);

    /**
     * Consulta Nativa (SQL): Utilizada específicamente para la generación de la Boleta/Reporte.
     * Realiza un INNER JOIN entre la tabla de consumos y el catálogo de servicios para 
     * obtener nombres legibles de los servicios consumidos.
     * * @param reservationId El ID de la reserva de la cual queremos generar el detalle.
     * @return Una lista de arreglos de objetos (Object[]) que contienen las columnas necesarias para el PDF.
     */
    @Query(value = "SELECT c.amount, s.service_name, c.unit_price, c.subtotal " +
                   "FROM consumption c " + 
                   "INNER JOIN services_catalog s ON c.service_id = s.service_id " +
                   "WHERE c.reservation_id = :reservationId", nativeQuery = true)
    List<Object[]> getReceiptDetails(@Param("reservationId") Integer reservationId);
}