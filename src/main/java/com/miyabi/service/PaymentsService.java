package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.miyabi.models.Payments;
import com.miyabi.repository.PaymentsRepository;

/**
 * Servicio encargado de gestionar la lógica de negocio de los pagos.
 * Actúa como la capa intermedia para procesar transacciones financieras
 * asociadas a las estadías en el hotel Miyabi.
 */
@Service
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;

    /**
     * Inyección de dependencias por constructor.
     * Garantiza que el repositorio esté disponible para todas las operaciones financieras.
     */
    public PaymentsService(PaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    /**
     * Recupera el historial completo de pagos registrados en el sistema.
     * Esencial para reportes de ingresos globales y cierres de caja.
     * @return Lista de todos los registros de pago.
     */
    public List<Payments> findAll() {
        return paymentsRepository.findAll();
    }

    /**
     * Lógica de consulta por Reserva:
     * Permite verificar si una reserva específica ya cuenta con un comprobante de pago emitido.
     * @param reservationId ID de la reserva a consultar.
     * @return El objeto Payments vinculado, facilitando la auditoría de cobros.
     */
    public Payments findByReservationId(Integer reservationId) {
        return paymentsRepository.findByReservation_ReservationId(reservationId);
    }

    /**
     * Registra un nuevo pago en el sistema.
     * Se invoca en el momento del "Check-out" o liquidación de la cuenta del huésped.
     * @param payment Objeto con la información del monto, método y estado de pago.
     * @return El registro del pago persistido en la base de datos.
     */
    public Payments save(Payments payment) {
        return paymentsRepository.save(payment);
    }
}