package com.miyabi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.miyabi.models.Guest;
import com.miyabi.service.GuestService;

import java.util.Map;
import java.util.HashMap;

/**
 * Controlador REST encargado de la autenticación de los clientes (Huéspedes).
 * Maneja el inicio de sesión, el registro, la verificación de sesión activa y el cierre de sesión.
 */
@RestController // Indica que los métodos de esta clase devolverán datos (JSON) y no vistas HTML.
@RequestMapping("/api/auth") // Ruta base para todos los endpoints de autenticación.
public class AuthController {

    // Dependencia del servicio que maneja la lógica de la base de datos para los huéspedes.
    private final GuestService guestService;

    /**
     * Constructor para la Inyección de Dependencias.
     */
    public AuthController(GuestService guestService) {
        this.guestService = guestService;
    }

    /**
     * Endpoint POST: /api/auth/login
     * Permite a un huésped iniciar sesión en el sistema.
     * * @param credentials Un mapa (JSON) que recibe el frontend con "email" y "password".
     * @param session Objeto de Spring que maneja la sesión actual del usuario en el navegador.
     * @return Respuesta JSON con los datos del usuario si es exitoso, o error 401 si falla.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String email = credentials.get("email"); 
        String password = credentials.get("password");

        // Llama al servicio para validar si el correo y la contraseña coinciden
        Guest authenticatedGuest = guestService.authenticate(email, password);

        if (authenticatedGuest != null) {
            // Si el usuario existe, guardamos su ID en la sesión del servidor
            session.setAttribute("guestId", authenticatedGuest.getIdGuest());
            
            // Preparamos la respuesta JSON para enviar al frontend
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login exitoso");
            response.put("guestName", authenticatedGuest.getNames() + " " + authenticatedGuest.getSurnames());
            response.put("guestId", authenticatedGuest.getIdGuest()); 
            
            return ResponseEntity.ok(response);
        } else {
            // Si las credenciales son incorrectas, devuelve un estado HTTP 401 (No Autorizado)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o contraseña incorrectos");
        }
    }
    
    /**
     * Endpoint POST: /api/auth/register
     * Permite registrar un nuevo huésped en la base de datos y loguearlo automáticamente.
     * * @param guest Objeto Huésped con todos los datos del formulario de registro.
     * @return Respuesta JSON confirmando el registro o un error 400 si algo falla (ej. DNI duplicado).
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Guest guest, HttpSession session) {
        try {
            // Guarda el nuevo huésped en la base de datos
            Guest savedGuest = guestService.save(guest);

            // Inicia sesión automáticamente guardando el ID en la sesión
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
    
    /**
     * Endpoint GET: /api/auth/check
     * El frontend llama a esta ruta cada vez que recarga la página para saber si el usuario
     * sigue logueado o si su sesión ya expiró.
     * * @return JSON indicando { "isLoggedIn": true/false } junto con los datos del usuario si está activo.
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkSession(HttpSession session) {
        // Busca si hay un ID de huésped guardado en la sesión actual
        Integer guestId = (Integer) session.getAttribute("guestId");
        
        if (guestId != null) {
            // Si hay sesión, busca los datos actualizados del huésped en la base de datos
            Guest guest = guestService.findById(guestId);
            if (guest != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("isLoggedIn", true);
                response.put("guestName", guest.getNames() + " " + guest.getSurnames());
                response.put("guestId", guest.getIdGuest());
                return ResponseEntity.ok(response);
            }
        }
        // Si no hay sesión o el ID es nulo, devuelve false
        return ResponseEntity.ok(Map.of("isLoggedIn", false));
    }

    /**
     * Endpoint POST: /api/auth/logout
     * Cierra la sesión del usuario actual.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        // Destruye completamente la sesión del servidor (borra el guestId)
        session.invalidate();
        return ResponseEntity.ok("Sesión cerrada");
    }
    
    /**
     * Endpoint GET: /api/auth/register (Vista)
     * OJO: Como esta clase es un @RestController, este método devolverá el texto plano "auth/Register"
     * en lugar de renderizar la vista HTML de Thymeleaf. 
     * Recomendación: Este método debería moverse a un @Controller normal (como tu HomeController).
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "auth/Register";
    }
}