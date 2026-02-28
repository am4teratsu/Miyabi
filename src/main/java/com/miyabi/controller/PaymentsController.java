package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.Payments;
import com.miyabi.service.PaymentsService;

/**
 * Controlador REST para gestionar los pagos y transacciones del hotel.
 * Maneja el registro de los cobros realizados y las consultas del historial de pagos
 * asociados a las reservas.
 */
@RestController // Indica que esta clase expone una API REST, devolviendo los datos en formato JSON.
@RequestMapping("/api/payments") // Define la ruta URL base para todos los endpoints de pagos.
public class PaymentsController {

    // Dependencia del servicio que contiene la lógica de negocio y comunicación con la tabla de pagos en la base de datos.
    private final PaymentsService paymentsService;

    /**
     * Constructor para la Inyección de Dependencias.
     * Spring Boot se encarga de instanciar y pasar el PaymentsService automáticamente.
     */
    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    /**
     * Endpoint GET: /api/payments
     * Obtiene el listado completo de todos los pagos registrados en el sistema.
     * Útil para generar reportes financieros o llenar tablas en el panel de administración.
     * @return Lista JSON con todos los objetos de tipo Payments.
     */
    @GetMapping
    public List<Payments> getAllPayments() {
        return paymentsService.findAll();
    }

    /**
     * Endpoint GET: /api/payments/reservation/{reservationId}
     * Busca el detalle de pago de una reserva en específico.
     * Muy útil para la generación de la boleta o para que el cliente vea el estado de cuenta
     * en su panel de "Mis Reservas".
     * @param reservationId El ID de la reserva, extraído dinámicamente de la URL (@PathVariable).
     * @return El objeto Payments correspondiente en formato JSON (monto, método de pago, fecha, etc.).
     */
    @GetMapping("/reservation/{reservationId}")
    public Payments getPaymentByReservation(@PathVariable Integer reservationId) {
        return paymentsService.findByReservationId(reservationId);
    }

    /**
     * Endpoint POST: /api/payments
     * Registra un nuevo pago en la base de datos.
     * Este endpoint es el que se llama al finalizar el proceso de Checkout web o 
     * cuando el recepcionista cobra en el mostrador del hotel.
     * @param payment Objeto mapeado desde el JSON recibido en la petición HTTP con los datos de la transacción (@RequestBody).
     * @return El pago guardado, incluyendo el ID único generado en la base de datos.
     */
    @PostMapping
    public Payments createPayment(@RequestBody Payments payment) {
        return paymentsService.save(payment);
    }
}