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

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final PaymentsRepository paymentsRepository;

    public ReservationController(ReservationService reservationService, PaymentsRepository paymentsRepository) {
        this.reservationService = reservationService;
        this.paymentsRepository = paymentsRepository;
    }
    
    @GetMapping("/availability")
    public ResponseEntity<Map<String, Object>> getMonthAvailability(@RequestParam int year, @RequestParam int month) {

        Map<String, Object> mockResponse = new HashMap<>();
        
        Map<String, Object> dayInfo = new HashMap<>();
        dayInfo.put("available", true);
        dayInfo.put("minPrice", 165000);
        
        mockResponse.put("2026-02-25", dayInfo);
        
        return ResponseEntity.ok(mockResponse);
    }

    @GetMapping("/unavailable-dates")
    public ResponseEntity<List<String>> getUnavailableDates() {
        List<Reservation> activeReservations = reservationService.findAll().stream()
                .filter(r -> !"Cancelled".equalsIgnoreCase(r.getState()))
                .collect(Collectors.toList());

        Map<LocalDate, Integer> dailyOccupancy = new HashMap<>();

        for (Reservation res : activeReservations) {
            LocalDate current = res.getEntryDate();

            while (current.isBefore(res.getDepartureDate())) {
                dailyOccupancy.put(current, dailyOccupancy.getOrDefault(current, 0) + 1);
                current = current.plusDays(1);
            }
        }

        int TOTAL_ROOMS = 6;
        List<String> unavailableDates = new ArrayList<>();

        for (Map.Entry<LocalDate, Integer> entry : dailyOccupancy.entrySet()) {
            if (entry.getValue() >= TOTAL_ROOMS) {
                unavailableDates.add(entry.getKey().toString()); 
            }
        }

        return ResponseEntity.ok(unavailableDates);
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.findAll();
    }

    @GetMapping("/guest/{idGuest}")
    public List<Reservation> getReservationsByGuest(@PathVariable Integer idGuest) {
        return reservationService.findByGuest_IdGuest(idGuest);
    }

    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationService.createReservation(reservation);
    }
    
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(@RequestBody Map<String, Object> payload) {
        try {
            Reservation reservation = reservationService.createReservationFromMap(payload);

            Payments payment = new Payments();
            payment.setReservation(reservation);
            payment.setTotalAmount(reservation.getTotalPay());
            payment.setPaymentMethod((String) payload.get("paymentMethod"));
            payment.setObservation("Reserva confirmada vía web");
            payment.setPaymentStatus("Paid"); 
            
            paymentsRepository.save(payment);

            return ResponseEntity.ok(Map.of(
                "message", "Reserva confirmada con éxito",
                "reservationCode", reservation.getReservationCode()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}