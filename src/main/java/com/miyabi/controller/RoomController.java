package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.Room;
import com.miyabi.service.RoomService;

/**
 * Controlador REST para la gestión del inventario de habitaciones (Rooms).
 * Proporciona los endpoints necesarios para que tanto el panel de administración
 * como el motor de reservas consulten y modifiquen las habitaciones del hotel.
 */
@RestController // Indica que los métodos de esta clase devolverán datos (JSON) directamente al cliente.
@RequestMapping("/api/rooms") // Define la ruta base para todos los endpoints relacionados con habitaciones.
public class RoomController {

    // Dependencia del servicio que contiene la lógica de negocio y las consultas a la base de datos de habitaciones.
    private final RoomService roomService;

    /**
     * Constructor para la Inyección de Dependencias.
     * Spring Boot instancia automáticamente el RoomService y lo inyecta aquí.
     */
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Endpoint GET: /api/rooms
     * Obtiene la lista completa de todas las habitaciones del hotel, sin importar su estado.
     * Útil principalmente para el panel de administración, donde el gerente necesita ver todo el inventario
     * (habitaciones ocupadas, en mantenimiento y disponibles).
     * @return Lista JSON con todos los objetos de tipo Room.
     */
    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.findAll();
    }

    /**
     * Endpoint GET: /api/rooms/available
     * Obtiene una lista filtrada solo con las habitaciones que están actualmente disponibles.
     * Este método es vital para el motor de reservas del frontend (el widget de búsqueda),
     * ya que garantiza que el cliente no pueda reservar una habitación ya ocupada.
     * @return Lista JSON con las habitaciones en estado "disponible".
     */
    @GetMapping("/available")
    public List<Room> getAvailableRooms() {
        return roomService.findAvailableRooms();
    }

    /**
     * Endpoint GET: /api/rooms/{id}
     * Busca los detalles específicos de una sola habitación utilizando su número de ID.
     * Muy útil para la pantalla de "Checkout" o cuando el cliente hace clic en "Ver detalles"
     * de una habitación específica en el catálogo.
     * @param id El identificador único de la habitación, extraído de la URL (@PathVariable).
     * @return El objeto Room correspondiente en formato JSON.
     */
    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable Integer id) {
        return roomService.findById(id);
    }

    /**
     * Endpoint POST: /api/rooms
     * Permite registrar una nueva habitación en el inventario del hotel.
     * Este endpoint está pensado para ser utilizado desde el panel de administrador cuando
     * el hotel habilita nuevas áreas o modifica su capacidad.
     * @param room Objeto con los datos de la nueva habitación mapeado desde el JSON recibido (@RequestBody).
     * @return La habitación creada y guardada en la base de datos.
     */
    @PostMapping
    public Room createRoom(@RequestBody Room room) {
        return roomService.save(room);
    }
}