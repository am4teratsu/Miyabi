package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.miyabi.models.Reservation;
import com.miyabi.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
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
}