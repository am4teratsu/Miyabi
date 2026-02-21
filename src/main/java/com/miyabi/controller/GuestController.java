package com.miyabi.controller;

import java.util.List;
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

    @GetMapping("/dni/{dni}")
    public Guest getGuestByDni(@PathVariable String dni) {
        return guestService.findByDni(dni);
    }

    @PostMapping
    public Guest createGuest(@RequestBody Guest guest) {
        return guestService.save(guest);
    }
}