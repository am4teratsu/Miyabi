package com.miyabi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.miyabi.models.Guest;
import com.miyabi.service.GuestService;

/**
 * Controlador REST para la gestión de Huéspedes (Guests).
 * Proporciona los endpoints necesarios para consultar, crear y actualizar 
 * los perfiles de los clientes que se registran en el hotel.
 */
@RestController // Indica que esta clase expone una API REST y devuelve datos en formato JSON.
@RequestMapping("/api/guests") // Ruta base para todos los endpoints relacionados con huéspedes.
public class GuestController {

    // Dependencia del servicio que contiene la lógica de negocio y acceso a la base de datos de Guests.
    private final GuestService guestService;

    /**
     * Constructor para la Inyección de Dependencias.
     * Spring Boot se encarga de proporcionar la instancia de GuestService.
     */
    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    /**
     * Endpoint GET: /api/guests
     * Obtiene la lista completa de todos los huéspedes registrados en el sistema.
     * @return Lista JSON con todos los objetos Guest.
     */
    @GetMapping
    public List<Guest> getAllGuests() {
        return guestService.findAll();
    }

    /**
     * Endpoint GET: /api/guests/{id}
     * Busca los datos de un huésped específico utilizando su ID (clave primaria).
     * @param id El identificador único del huésped, extraído de la URL (@PathVariable).
     * @return El objeto Guest correspondiente en formato JSON.
     */
    @GetMapping("/{id}")
    public Guest getGuestById(@PathVariable Integer id) {
        return guestService.findById(id);
    }
    
    /**
     * Endpoint PUT: /api/guests/{id}
     * Actualiza los datos del perfil de un huésped existente (ej. desde la vista "Mi Perfil").
     * Utiliza ResponseEntity para devolver códigos de estado HTTP adecuados (200 OK, 404 Not Found, 400 Bad Request).
     * * @param id El ID del huésped a modificar.
     * @param updatedGuest Objeto con los nuevos datos recibidos en el cuerpo de la petición.
     * @return El objeto Guest actualizado o un mensaje de error.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGuest(@PathVariable Integer id, @RequestBody Guest updatedGuest) {
        try {
            // 1. Verificamos si el huésped realmente existe en la base de datos
            Guest existingGuest = guestService.findById(id);
            if (existingGuest == null) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

            // 2. Actualizamos solo los campos permitidos con los nuevos valores
            existingGuest.setNames(updatedGuest.getNames());
            existingGuest.setSurnames(updatedGuest.getSurnames());
            existingGuest.setEmail(updatedGuest.getEmail());
            existingGuest.setPhone(updatedGuest.getPhone());

            // 3. Lógica de seguridad para la contraseña:
            // Solo se actualiza si el usuario envió una nueva contraseña válida (no nula ni vacía).
            // Esto evita que la contraseña se borre si el usuario solo actualizó su nombre o teléfono.
            if (updatedGuest.getPassword() != null && !updatedGuest.getPassword().isEmpty()) {
                existingGuest.setPassword(updatedGuest.getPassword());
            }

            // 4. Guardamos los cambios en la base de datos
            Guest savedGuest = guestService.save(existingGuest);
            return ResponseEntity.ok(savedGuest);

        } catch (Exception e) {
            // Si ocurre algún error (ej. email duplicado), devolvemos un estado 400 con el detalle
            return ResponseEntity.badRequest().body("Error al actualizar: " + e.getMessage());
        }
    }

    /**
     * Endpoint GET: /api/guests/dni/{dni}
     * Busca a un huésped utilizando su documento de identidad.
     * Muy útil para el proceso de Checkout: permite verificar si el cliente ya existe
     * en el sistema antes de crearle una reserva nueva.
     * * @param dni El número de documento a buscar.
     * @return El objeto Guest si se encuentra, o nulo si no existe.
     */
    @GetMapping("/dni/{dni}")
    public Guest getGuestByDni(@PathVariable String dni) {
        return guestService.findByDni(dni);
    }

    /**
     * Endpoint POST: /api/guests
     * Crea un nuevo huésped manualmente en la base de datos.
     * (Nota: El registro desde la web suele pasar por AuthController, pero este endpoint 
     * es útil para que un recepcionista registre a un cliente desde el panel de administración).
     * * @param guest Los datos del nuevo huésped.
     * @return El huésped guardado con su ID generado.
     */
    @PostMapping
    public Guest createGuest(@RequestBody Guest guest) {
        return guestService.save(guest);
    }
}