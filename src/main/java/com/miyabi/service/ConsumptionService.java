package com.miyabi.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

import com.miyabi.models.Consumption;
import com.miyabi.repository.ConsumptionRepository;

/**
 * Servicio encargado de gestionar los consumos adicionales de los huéspedes.
 * Centraliza la lógica de cálculo de montos para servicios como Room Service, Spa, etc.
 */
@Service
public class ConsumptionService {

    private final ConsumptionRepository consumptionRepository;

    // Inyección de dependencias por constructor
    public ConsumptionService(ConsumptionRepository consumptionRepository) {
        this.consumptionRepository = consumptionRepository;
    }

    /**
     * Lista todos los consumos registrados en el sistema (Historial global).
     */
    public List<Consumption> findAll() {
        return consumptionRepository.findAll();
    }

    /**
     * Busca un consumo específico por su identificador único.
     */
    public Consumption findById(Integer id) {
        return consumptionRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene todos los consumos vinculados a una reserva en particular.
     * Fundamental para el proceso de Checkout, donde se debe sumar todo lo extra al total.
     */
    public List<Consumption> findByReservationId(Integer reservationId) {
        return consumptionRepository.findByReservation_ReservationId(reservationId);
    }

    /**
     * LÓGICA DE NEGOCIO: Guarda un consumo y calcula automáticamente el subtotal.
     * Implementa la fórmula: Subtotal = Cantidad * Precio Unitario.
     * @param consumption El objeto consumo enviado desde el formulario.
     * @return El consumo guardado con el subtotal ya procesado.
     */
    public Consumption save(Consumption consumption) {
        
        // Validación y cálculo automático para asegurar integridad financiera
        if (consumption.getAmount() != null && consumption.getUnitPrice() != null) {
            BigDecimal amountStr = new BigDecimal(consumption.getAmount());
            
            // Operación con BigDecimal para mantener la precisión de los decimales
            BigDecimal calculatedSubtotal = consumption.getUnitPrice().multiply(amountStr);
            
            // Asignación automática del subtotal antes de persistir en la base de datos
            consumption.setSubtotal(calculatedSubtotal);
        }
        
        return consumptionRepository.save(consumption);
    }
}