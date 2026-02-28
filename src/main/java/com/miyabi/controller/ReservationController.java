package com.miyabi.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.miyabi.models.Payments;
import com.miyabi.models.Reservation;
import com.miyabi.repository.PaymentsRepository;
import com.miyabi.service.ReservationService;

/**
 * Controlador REST principal para el motor de reservas.
 * Gestiona la disponibilidad del calendario, la creación de nuevas reservas
 * desde el Checkout y la consulta del historial de los huéspedes.
 */
@RestController // Indica que es una API REST, devuelve respuestas en formato JSON.
@RequestMapping("/api/reservations") // Ruta base para los endpoints de reservas.
public class ReservationController {

    // Dependencias inyectadas: El servicio para lógica de reservas y el repositorio para guardar pagos.
    private final ReservationService reservationService;
    private final PaymentsRepository paymentsRepository;

    /**
     * Constructor para la Inyección de Dependencias.
     */
    public ReservationController(ReservationService reservationService, PaymentsRepository paymentsRepository) {
        this.reservationService = reservationService;
        this.paymentsRepository = paymentsRepository;
    }
    
    /**
     * Endpoint GET: /api/reservations/availability
     * Devuelve la disponibilidad y precios base para un mes y año en específico.
     * (Nota técnica: Actualmente devuelve datos simulados (mock) para pruebas de frontend.
     * En el futuro, aquí se consultaría la base de datos).
     */
    @GetMapping("/availability")
    public ResponseEntity<Map<String, Object>> getMonthAvailability(@RequestParam int year, @RequestParam int month) {

        Map<String, Object> mockResponse = new HashMap<>();
        
        Map<String, Object> dayInfo = new HashMap<>();
        dayInfo.put("available", true);
        dayInfo.put("minPrice", 165000);
        
        mockResponse.put("2026-02-25", dayInfo);
        
        return ResponseEntity.ok(mockResponse);
    }

    /**
     * Endpoint GET: /api/reservations/unavailable-dates
     * Algoritmo que calcula qué fechas están 100% ocupadas para bloquearlas en el calendario del frontend.
     * @return Lista de fechas (en formato String) que ya no tienen habitaciones disponibles.
     */
    @GetMapping("/unavailable-dates")
    public ResponseEntity<List<String>> getUnavailableDates() {
        // 1. Obtiene todas las reservas que NO estén canceladas
        List<Reservation> activeReservations = reservationService.findAll().stream()
                .filter(r -> !"Cancelled".equalsIgnoreCase(r.getState()))
                .collect(Collectors.toList());

        // Mapa para contar cuántas habitaciones están ocupadas en cada fecha
        Map<LocalDate, Integer> dailyOccupancy = new HashMap<>();

        // 2. Recorre cada reserva y suma +1 a cada día comprendido entre el check-in y el check-out
        for (Reservation res : activeReservations) {
            LocalDate current = res.getEntryDate();

            while (current.isBefore(res.getDepartureDate())) {
                dailyOccupancy.put(current, dailyOccupancy.getOrDefault(current, 0) + 1);
                current = current.plusDays(1);
            }
        }

        // 3. Verifica contra el inventario total del hotel (6 habitaciones en este caso)
        int TOTAL_ROOMS = 6;
        List<String> unavailableDates = new ArrayList<>();

        // Si una fecha tiene 6 o más ocupaciones, se agrega a la lista de bloqueadas
        for (Map.Entry<LocalDate, Integer> entry : dailyOccupancy.entrySet()) {
            if (entry.getValue() >= TOTAL_ROOMS) {
                unavailableDates.add(entry.getKey().toString()); 
            }
        }

        return ResponseEntity.ok(unavailableDates);
    }

    /**
     * Endpoint GET: /api/reservations
     * Obtiene todas las reservas históricas y actuales del sistema.
     */
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.findAll();
    }

    /**
     * Endpoint GET: /api/reservations/guest/{idGuest}
     * Obtiene todas las reservas de un cliente en específico.
     * * FUNDAMENTAL para poblar dinámicamente la vista "Mis Reservas" (MyReservations.html).
     */
    @GetMapping("/guest/{idGuest}")
    public List<Reservation> getReservationsByGuest(@PathVariable Integer idGuest) {
        return reservationService.findByGuest_IdGuest(idGuest);
    }

    /**
     * Endpoint POST: /api/reservations
     * Creación estándar de una reserva (generalmente usado por el panel de administración).
     */
    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationService.createReservation(reservation);
    }
    
    /**
     * Endpoint POST: /api/reservations/confirm
     * Método principal del Checkout. Recibe el JSON completo desde el frontend, 
     * guarda la reserva y automáticamente registra el pago inicial.
     * @param payload Mapa JSON con los datos del huésped, fechas, habitación y método de pago.
     * @return Respuesta confirmando el éxito de la operación junto con el código generado (ej. RES-2026-0005).
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Delega la creación de la reserva y del huésped (si es nuevo) al servicio
            Reservation reservation = reservationService.createReservationFromMap(payload);

            // 2. Registra automáticamente el pago asociado a esta reserva
            Payments payment = new Payments();
            payment.setReservation(reservation);
            payment.setTotalAmount(reservation.getTotalPay());
            payment.setPaymentMethod((String) payload.get("paymentMethod"));
            payment.setObservation("Reserva confirmada vía web");
            payment.setPaymentStatus("Paid"); 
            
            paymentsRepository.save(payment);

            // 3. Devuelve el éxito y el código de reserva para mostrarlo en el frontend
            return ResponseEntity.ok(Map.of(
                "message", "Reserva confirmada con éxito",
                "reservationCode", reservation.getReservationCode()
            ));
        } catch (Exception e) {
            // En caso de error (ej. base de datos caída), devuelve un HTTP 400 con el motivo
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}