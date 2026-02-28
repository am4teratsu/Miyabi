package com.miyabi.controller;

import com.miyabi.models.Reservation;
import com.miyabi.models.Room;
import com.miyabi.models.Guest;
import com.miyabi.service.ReservationService;
import com.miyabi.service.RoomService;
import com.miyabi.service.GuestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/users")
public class UserReservationController {

    private final ReservationService reservationService;
    private final RoomService roomService;
    private final GuestService guestService;

    public UserReservationController(ReservationService reservationService,
                                     RoomService roomService,
                                     GuestService guestService) {
        this.reservationService = reservationService;
        this.roomService        = roomService;
        this.guestService       = guestService;
    }

    // ── 1. LISTAR todas las reservas ────────────────────────────────────────
    // URL: GET /users/reservations
    // Retorna la vista: templates/users/Reservations.html
    @GetMapping("/reservations")
    public String listReservations(Model model) {
        model.addAttribute("reservations", reservationService.findAll());
        model.addAttribute("rooms",        roomService.findAll());
        model.addAttribute("guests",       guestService.findAll());
        return "users/Reservations";
    }

    // ── 2. MOSTRAR formulario de edición ───────────────────────────────────
    // URL: GET /users/reservations/edit/5
    // Retorna la vista: templates/users/fragments/ReservationForm.html
    @GetMapping("/reservations/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Reservation reservation = reservationService.findById(id);
        if (reservation == null) return "redirect:/users/reservations";

        model.addAttribute("reservation", reservation);
        model.addAttribute("rooms",  roomService.findAll());
        model.addAttribute("guests", guestService.findAll());
        model.addAttribute("isEdit", true);
        return "users/fragments/ReservationForm";
    }

    // ── 3. CREAR nueva reserva ─────────────────────────────────────────────
    // URL: POST /users/reservations/save
    // Viene del formulario en el modal de Reservations.html
    @PostMapping("/reservations/save")
    public String save(@RequestParam Integer roomId,
                       @RequestParam Integer guestId,
                       @RequestParam String entryDate,
                       @RequestParam String departureDate,
                       @RequestParam(defaultValue = "1")  Integer numAdults,
                       @RequestParam(defaultValue = "0")  Integer numChildren,
                       @RequestParam(defaultValue = "Pending") String state,
                       @RequestParam(required = false) String observations,
                       RedirectAttributes ra) {
        try {
            Room  room  = roomService.findById(roomId);
            Guest guest = guestService.findById(guestId);

            LocalDate in    = LocalDate.parse(entryDate);
            LocalDate out   = LocalDate.parse(departureDate);
            long nights     = ChronoUnit.DAYS.between(in, out);
            BigDecimal price    = room.getRoomType().getBasePrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(nights));

            Reservation res = new Reservation();
            res.setRoom(room);
            res.setGuest(guest);
            res.setEntryDate(in);
            res.setDepartureDate(out);
            res.setNumberNights((int) nights);
            res.setPricePerNight(price);
            res.setRoomSubtotal(subtotal);
            res.setTotalConsumption(BigDecimal.ZERO);
            res.setTotalPay(subtotal);
            res.setNumAdults(numAdults);
            res.setNumChildren(numChildren);
            res.setState(state);
            res.setObservations(observations);

            // Generar código único con número aleatorio (evita duplicados por tamaño de lista)
            String year = String.valueOf(LocalDate.now().getYear());
            String code;
            do {
                int rand = (int)(Math.random() * 9000) + 1000;
                code = "RES-" + year + "-" + rand;
            } while (reservationService.findByCode(code) != null);
            res.setReservationCode(code);

            reservationService.saveFromEmployee(res);
            ra.addFlashAttribute("success", "Reservation created successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/users/reservations";
    }

    // ── 4. ACTUALIZAR reserva existente ────────────────────────────────────
    // URL: POST /users/reservations/update/5
    // Viene del formulario en ReservationForm.html
    @PostMapping("/reservations/update/{id}")
    public String update(@PathVariable Integer id,
                         @RequestParam Integer roomId,
                         @RequestParam Integer guestId,
                         @RequestParam String entryDate,
                         @RequestParam String departureDate,
                         @RequestParam(defaultValue = "1") Integer numAdults,
                         @RequestParam(defaultValue = "0") Integer numChildren,
                         @RequestParam String state,
                         @RequestParam(required = false) String observations,
                         RedirectAttributes ra) {
        try {
            Reservation res = reservationService.findById(id);
            if (res == null) throw new RuntimeException("Reservation not found");

            Room  room  = roomService.findById(roomId);
            Guest guest = guestService.findById(guestId);

            LocalDate in    = LocalDate.parse(entryDate);
            LocalDate out   = LocalDate.parse(departureDate);
            long nights     = ChronoUnit.DAYS.between(in, out);
            BigDecimal price    = room.getRoomType().getBasePrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(nights));
            BigDecimal consumo  = res.getTotalConsumption() != null
                                    ? res.getTotalConsumption() : BigDecimal.ZERO;

            res.setRoom(room);
            res.setGuest(guest);
            res.setEntryDate(in);
            res.setDepartureDate(out);
            res.setNumberNights((int) nights);
            res.setPricePerNight(price);
            res.setRoomSubtotal(subtotal);
            res.setTotalPay(subtotal.add(consumo));
            res.setNumAdults(numAdults);
            res.setNumChildren(numChildren);
            res.setState(state);
            res.setObservations(observations);

            reservationService.saveFromEmployee(res);
            ra.addFlashAttribute("success", "Reservation updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/users/reservations";
    }

    // ── 5. ELIMINAR reserva ────────────────────────────────────────────────
    // URL: POST /users/reservations/delete/5
    // Viene del modal de confirmación en Reservations.html
    @PostMapping("/reservations/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            reservationService.deleteById(id);
            ra.addFlashAttribute("success", "Reservation deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Cannot delete: " + e.getMessage());
        }
        return "redirect:/users/reservations";
    }
}