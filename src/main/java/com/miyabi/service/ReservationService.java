package com.miyabi.service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miyabi.models.Reservation;
import com.miyabi.models.Room;
import com.miyabi.repository.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomService roomService;

    public ReservationService(ReservationRepository reservationRepository, RoomService roomService) {
        this.reservationRepository = reservationRepository;
        this.roomService = roomService;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Reservation findById(Integer id) {
        return reservationRepository.findById(id).orElse(null);
    }
    
    public List<Reservation> findByGuest_IdGuest(Integer idGuest) {
        return reservationRepository.findByGuest_IdGuest(idGuest);
    }

    @Transactional
    public Reservation createReservation(Reservation reservation) {

        Room roomToReserve = roomService.findById(reservation.getRoom().getIdRoom());
        if (roomToReserve == null || !roomToReserve.getState().equals("Available")) {
            throw new RuntimeException("Room is not available.");
        }

        long nights = ChronoUnit.DAYS.between(reservation.getEntryDate(), reservation.getDepartureDate());
        reservation.setNumberNights((int) nights);

        BigDecimal nightsDecimal = new BigDecimal(nights);
        BigDecimal subtotal = reservation.getPricePerNight().multiply(nightsDecimal);
        reservation.setRoomSubtotal(subtotal);
        reservation.setTotalPay(subtotal); 

        String code = "RES-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        reservation.setReservationCode(code);
        reservation.setState("Pending");

        roomToReserve.setState("Reserved");
        roomService.save(roomToReserve); 

        return reservationRepository.save(reservation);
    }
}