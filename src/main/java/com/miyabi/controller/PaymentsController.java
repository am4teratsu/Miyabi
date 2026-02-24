package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.Payments;
import com.miyabi.service.PaymentsService;

@RestController
@RequestMapping("/api/payments")
public class PaymentsController {

    private final PaymentsService paymentsService;

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @GetMapping
    public List<Payments> getAllPayments() {
        return paymentsService.findAll();
    }

    @GetMapping("/reservation/{reservationId}")
    public Payments getPaymentByReservation(@PathVariable Integer reservationId) {
        return paymentsService.findByReservationId(reservationId);
    }

    @PostMapping
    public Payments createPayment(@RequestBody Payments payment) {
        return paymentsService.save(payment);
    }
}