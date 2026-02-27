package com.miyabi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miyabi.models.Guest;
import com.miyabi.models.Reservation;
import com.miyabi.models.Room;
import com.miyabi.repository.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomService roomService;
    private final GuestService guestService;

    public ReservationService(ReservationRepository reservationRepository, RoomService roomService, GuestService guestService) {
        this.reservationRepository = reservationRepository;
        this.roomService = roomService;
        this.guestService = guestService;
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

        if (reservation.getGuest() != null && reservation.getGuest().getIdGuest() != null) {
            Guest existingGuest = guestService.findById(reservation.getGuest().getIdGuest());
            
            if (existingGuest != null) {
                Guest incomingData = reservation.getGuest();
                if (incomingData.getPhone() != null) existingGuest.setPhone(incomingData.getPhone());
                if (incomingData.getMobilePhone() != null) existingGuest.setMobilePhone(incomingData.getMobilePhone());
                if (incomingData.getAddress() != null) existingGuest.setAddress(incomingData.getAddress());
                if (incomingData.getCountry() != null) existingGuest.setCountry(incomingData.getCountry());
                if (incomingData.getCity() != null) existingGuest.setCity(incomingData.getCity());
                if (incomingData.getPostalCode() != null) existingGuest.setPostalCode(incomingData.getPostalCode());
 
                guestService.save(existingGuest);
                reservation.setGuest(existingGuest);
            } else {
                throw new RuntimeException("Cliente no encontrado en la base de datos.");
            }
        } else {
            throw new RuntimeException("La reserva debe estar asociada a un cliente logueado.");
        }

        int adults = reservation.getNumAdults() != null ? reservation.getNumAdults() : 1;
        int children = reservation.getNumChildren() != null ? reservation.getNumChildren() : 0;
        int totalGuests = adults + children;

        if (totalGuests > 6) {
            throw new RuntimeException("No aceptamos reservas para más de 6 personas en un grupo.");
        }
        if (adults < 1) {
            throw new RuntimeException("Debe haber al menos 1 adulto en la reserva.");
        }

        Room roomToReserve = roomService.findById(reservation.getRoom().getIdRoom());
        if (roomToReserve == null) {
            throw new RuntimeException("La habitación no existe.");
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

        return reservationRepository.save(reservation);
    }
    
    public Reservation createReservationFromMap(Map<String, Object> payload) {
        Reservation res = new Reservation();

        res.setEntryDate(LocalDate.parse((String) payload.get("entryDate")));
        res.setDepartureDate(LocalDate.parse((String) payload.get("departureDate")));
        res.setPricePerNight(new BigDecimal(payload.get("pricePerNight").toString()));
        res.setNumAdults((Integer) payload.get("numAdults"));
        res.setObservations((String) payload.get("observations"));

        Map<String, Object> guestMap = (Map<String, Object>) payload.get("guest");
        Guest guest = new Guest();
        guest.setIdGuest((Integer) guestMap.get("idGuest"));
        guest.setPhone((String) guestMap.get("phone"));
        guest.setMobilePhone((String) guestMap.get("mobilePhone"));
        guest.setAddress((String) guestMap.get("address"));
        guest.setCountry((String) guestMap.get("country"));
        guest.setCity((String) guestMap.get("city"));
        guest.setPostalCode((String) guestMap.get("postalCode"));
        res.setGuest(guest);

        Map<String, Object> roomMap = (Map<String, Object>) payload.get("room");
        Room room = new Room();
        room.setIdRoom((Integer) roomMap.get("idRoom"));
        res.setRoom(room);

        return this.createReservation(res);
    }
}