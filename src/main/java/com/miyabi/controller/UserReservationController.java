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

/**
 * Controlador MVC para la gestión de Reservas por parte de los empleados (Intranet).
 * A diferencia del ReservationController (API REST para el cliente final), 
 * este controlador renderiza vistas HTML para que los recepcionistas o administradores
 * puedan gestionar manualmente las reservas desde el panel de control.
 */
@Controller // Indica que es un controlador que devuelve plantillas Thymeleaf, no datos JSON.
@RequestMapping("/users") // Ruta base para el módulo administrativo de empleados.
public class UserReservationController {

    // Dependencias inyectadas necesarias para cruzar los datos de reservas, habitaciones y clientes.
    private final ReservationService reservationService;
    private final RoomService roomService;
    private final GuestService guestService;

    /**
     * Constructor para la Inyección de Dependencias.
     */
    public UserReservationController(ReservationService reservationService,
                                     RoomService roomService,
                                     GuestService guestService) {
        this.reservationService = reservationService;
        this.roomService        = roomService;
        this.guestService       = guestService;
    }

    // ── 1. LISTAR todas las reservas ────────────────────────────────────────
    /**
     * Endpoint GET: /users/reservations
     * Carga la vista principal con la tabla de todas las reservas registradas.
     * También envía la lista de habitaciones y huéspedes para poblar los selects de los modales de creación/edición.
     */
    @GetMapping("/reservations")
    public String listReservations(Model model) {
        model.addAttribute("reservations", reservationService.findAll());
        model.addAttribute("rooms",        roomService.findAll());
        model.addAttribute("guests",       guestService.findAll());
        return "users/Reservations";
    }

    // ── 2. MOSTRAR formulario de edición ───────────────────────────────────
    /**
     * Endpoint GET: /users/reservations/edit/{id}
     * Carga un fragmento HTML (formulario de edición) para una reserva en específico.
     * Esto es muy útil para cargarlo dinámicamente dentro de un modal en el frontend sin recargar toda la página.
     */
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
    /**
     * Endpoint POST: /users/reservations/save
     * Procesa el formulario para registrar una nueva reserva manualmente (ej. el cliente llamó por teléfono).
     * Utiliza el patrón PRG (Post-Redirect-Get) redireccionando a la lista al terminar.
     */
    @PostMapping("/reservations/save")
    public String save(@RequestParam Integer roomId,
                       @RequestParam Integer guestId,
                       @RequestParam String entryDate,
                       @RequestParam String departureDate,
                       @RequestParam(defaultValue = "1")  Integer numAdults,
                       @RequestParam(defaultValue = "0")  Integer numChildren,
                       @RequestParam(defaultValue = "Pending") String state,
                       @RequestParam(required = false) String observations,
                       RedirectAttributes ra) { // ra permite enviar mensajes temporales ("flash") a la vista redireccionada.
        try {
            Room  room  = roomService.findById(roomId);
            Guest guest = guestService.findById(guestId);

            // Cálculos automáticos de fechas y costos
            LocalDate in    = LocalDate.parse(entryDate);
            LocalDate out   = LocalDate.parse(departureDate);
            long nights     = ChronoUnit.DAYS.between(in, out); // Calcula los días exactos de estancia
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
            res.setTotalConsumption(BigDecimal.ZERO); // Como es nueva, aún no tiene consumos extra
            res.setTotalPay(subtotal);
            res.setNumAdults(numAdults);
            res.setNumChildren(numChildren);
            res.setState(state);
            res.setObservations(observations);

            // Lógica de generación de código único para la reserva (Ej. RES-2026-1548)
            String year = String.valueOf(LocalDate.now().getYear());
            String code;
            do {
                int rand = (int)(Math.random() * 9000) + 1000;
                code = "RES-" + year + "-" + rand;
            } while (reservationService.findByCode(code) != null); // Verifica que el código no exista ya en la BD
            res.setReservationCode(code);

            reservationService.saveFromEmployee(res);
            ra.addFlashAttribute("success", "Reservation created successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/users/reservations";
    }

    // ── 4. ACTUALIZAR reserva existente ────────────────────────────────────
    /**
     * Endpoint POST: /users/reservations/update/{id}
     * Procesa la actualización de los datos de una reserva (ej. el cliente extendió su estancia).
     */
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
            
            // Si la reserva ya tenía consumos de room service/spa, los mantiene para recalcular el pago final
            BigDecimal consumo  = res.getTotalConsumption() != null
                                    ? res.getTotalConsumption() : BigDecimal.ZERO;

            res.setRoom(room);
            res.setGuest(guest);
            res.setEntryDate(in);
            res.setDepartureDate(out);
            res.setNumberNights((int) nights);
            res.setPricePerNight(price);
            res.setRoomSubtotal(subtotal);
            res.setTotalPay(subtotal.add(consumo)); // Suma el costo de la habitación + los consumos
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
    /**
     * Endpoint POST: /users/reservations/delete/{id}
     * Elimina una reserva del sistema. 
     */
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