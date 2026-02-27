package com.miyabi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.miyabi.models.Guest;
import com.miyabi.service.GuestService;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GuestService guestService;

    public AuthController(GuestService guestService) {
        this.guestService = guestService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String email = credentials.get("email"); 
        String password = credentials.get("password");

        Guest authenticatedGuest = guestService.authenticate(email, password);

        if (authenticatedGuest != null) {
            session.setAttribute("guestId", authenticatedGuest.getIdGuest());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login exitoso");
            response.put("guestName", authenticatedGuest.getNames() + " " + authenticatedGuest.getSurnames());
            
            response.put("guestId", authenticatedGuest.getIdGuest()); 
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o contraseña incorrectos");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Guest guest, HttpSession session) {
        try {
            Guest savedGuest = guestService.save(guest);

            session.setAttribute("guestId", savedGuest.getIdGuest());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registro e inicio de sesión exitoso");
            response.put("guestName", savedGuest.getNames() + " " + savedGuest.getSurnames());
            response.put("guestId", savedGuest.getIdGuest());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al registrar: " + e.getMessage());
        }
    }
    
    @GetMapping("/check")
    public ResponseEntity<?> checkSession(HttpSession session) {
        Integer guestId = (Integer) session.getAttribute("guestId");
        
        if (guestId != null) {
            Guest guest = guestService.findById(guestId);
            if (guest != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("isLoggedIn", true);
                response.put("guestName", guest.getNames() + " " + guest.getSurnames());
                response.put("guestId", guest.getIdGuest());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.ok(Map.of("isLoggedIn", false));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Sesión cerrada");
    }
    
    @GetMapping("/register")
    public String showRegisterPage() {
        return "auth/Register";
    }
}