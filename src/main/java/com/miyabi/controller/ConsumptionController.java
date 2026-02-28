package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.Consumption;
import com.miyabi.service.ConsumptionService;

/**
 * Controlador REST para gestionar los consumos adicionales de los huéspedes.
 * Maneja operaciones como agregar cargos a una habitación (room service, spa, etc.)
 * y consultar los gastos asociados a una reserva.
 */
@RestController // Indica que es una API REST; las respuestas se serializan automáticamente a formato JSON.
@RequestMapping("/api/consumptions") // Define la ruta base para todos los endpoints de consumos.
public class ConsumptionController {

    // Dependencia del servicio que contiene la lógica de negocio para los consumos.
    private final ConsumptionService consumptionService;

    /**
     * Constructor para la Inyección de Dependencias.
     * Spring Boot proporciona la instancia de ConsumptionService automáticamente.
     */
    public ConsumptionController(ConsumptionService consumptionService) {
        this.consumptionService = consumptionService;
    }

    /**
     * Endpoint GET: /api/consumptions
     * Obtiene el historial completo de todos los consumos registrados en el hotel.
     * * @return Lista JSON con todos los objetos Consumption.
     */
    @GetMapping
    public List<Consumption> getAllConsumptions() {
        return consumptionService.findAll();
    }

    /**
     * Endpoint GET: /api/consumptions/reservation/{reservationId}
     * Busca y devuelve exclusivamente los consumos asociados a una reserva específica.
     * * NOTA PARA LA BOLETA: Este método es exactamente el que se necesita consultar
     * internamente (a nivel de servicio) para listar los detalles extra en el PDF de JasperReports.
     * * @param reservationId El ID de la reserva, extraído dinámicamente de la URL (@PathVariable).
     * @return Lista JSON con los consumos de esa reserva en particular.
     */
    @GetMapping("/reservation/{reservationId}")
    public List<Consumption> getConsumptionsByReservation(@PathVariable Integer reservationId) {
        return consumptionService.findByReservationId(reservationId);
    }

    /**
     * Endpoint POST: /api/consumptions
     * Registra un nuevo consumo en el sistema (ej. cuando un recepcionista agrega una bebida a la cuenta).
     * * @param consumption Objeto con los datos del consumo (cantidad, precio, ID de servicio, etc.) recibido en el cuerpo de la petición (@RequestBody).
     * @return El objeto Consumption guardado, incluyendo el ID generado por la base de datos.
     */
    @PostMapping
    public Consumption createConsumption(@RequestBody Consumption consumption) {
        return consumptionService.save(consumption);
    }
}