package com.miyabi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.miyabi.models.Guest;
import com.miyabi.service.GuestService;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public List<Guest> getAllGuests() {
        return guestService.findAll();
    }

    @GetMapping("/{id}")
    public Guest getGuestById(@PathVariable Integer id) {
        return guestService.findById(id);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGuest(@PathVariable Integer id, @RequestBody Guest updatedGuest) {
        try {
            Guest existingGuest = guestService.findById(id);
            if (existingGuest == null) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

            existingGuest.setNames(updatedGuest.getNames());
            existingGuest.setSurnames(updatedGuest.getSurnames());
            existingGuest.setEmail(updatedGuest.getEmail());
            existingGuest.setPhone(updatedGuest.getPhone());

            if (updatedGuest.getPassword() != null && !updatedGuest.getPassword().isEmpty()) {
                existingGuest.setPassword(updatedGuest.getPassword());
            }

            Guest savedGuest = guestService.save(existingGuest);
            return ResponseEntity.ok(savedGuest);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar: " + e.getMessage());
        }
    }

    @GetMapping("/dni/{dni}")
    public Guest getGuestByDni(@PathVariable String dni) {
        return guestService.findByDni(dni);
    }

    @PostMapping
    public Guest createGuest(@RequestBody Guest guest) {
        return guestService.save(guest);
    }
}