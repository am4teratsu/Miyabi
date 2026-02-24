package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.miyabi.models.Payments;
import com.miyabi.repository.PaymentsRepository;

@Service
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;

    public PaymentsService(PaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    public List<Payments> findAll() {
        return paymentsRepository.findAll();
    }

    public Payments findByReservationId(Integer reservationId) {
        return paymentsRepository.findByReservation_ReservationId(reservationId);
    }

    public Payments save(Payments payment) {
        return paymentsRepository.save(payment);
    }
}