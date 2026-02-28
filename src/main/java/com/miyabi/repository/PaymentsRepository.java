package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.Payments;

/**
 * Interfaz de Repositorio para la entidad Payments.
 * Gestiona la persistencia de las transacciones económicas realizadas en el sistema.
 */
public interface PaymentsRepository extends JpaRepository<Payments, Integer> {
    
    /**
     * Query Method: Busca el registro de pago asociado a una reserva específica.
     * Gracias a la convención de nombres de Spring Data JPA, el framework entiende 
     * que debe navegar desde la entidad 'Payments' hacia su atributo 'reservation' 
     * y filtrar por el campo 'reservationId'.
     * * @param reservationId Identificador único de la reserva.
     * @return El objeto Payments vinculado a dicha reserva.
     */
    Payments findByReservation_ReservationId(Integer reservationId);
}