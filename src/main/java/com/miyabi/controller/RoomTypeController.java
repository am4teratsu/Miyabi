package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.RoomType;
import com.miyabi.service.RoomTypeService;

/**
 * Controlador REST para la gestión de Tipos/Categorías de Habitación (RoomType).
 * Administra las diferentes categorías que ofrece el hotel (Ej: Suite Estilo Zen, 
 * Habitación Japonesa Premier) junto con sus descripciones y capacidades base.
 */
@RestController // Indica que es una API REST, devolviendo datos crudos en formato JSON.
@RequestMapping("/api/room-types") // Ruta base para los endpoints de las categorías de habitaciones.
public class RoomTypeController {

    // Dependencia del servicio que maneja la lógica y conexión con la base de datos para los tipos de habitación.
    private final RoomTypeService roomTypeService;

    /**
     * Constructor para la Inyección de Dependencias.
     * Spring Boot se encarga de inyectar la instancia de RoomTypeService automáticamente.
     */
    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    /**
     * Endpoint GET: /api/room-types
     * Obtiene el catálogo completo de todos los tipos de habitaciones del hotel.
     * Es utilizado tanto por el panel de administración como por el HomeController 
     * para dibujar las tarjetas dinámicas en la página pública de "/rooms".
     * @return Lista JSON con todas las categorías de habitaciones.
     */
    @GetMapping
    public List<RoomType> getAllRoomTypes() {
        return roomTypeService.findAll();
    }

    /**
     * Endpoint POST: /api/room-types
     * Crea un nuevo tipo o categoría de habitación en el sistema.
     * Utilizado desde el panel de administración cuando el hotel decide crear un
     * nuevo concepto de habitación.
     * @param roomType Objeto con los datos de la nueva categoría mapeado desde el JSON (@RequestBody).
     * @return La categoría creada y guardada, incluyendo su ID generado.
     */
    @PostMapping
    public RoomType createRoomType(@RequestBody RoomType roomType) {
        return roomTypeService.save(roomType);
    }
    
    /**
     * Endpoint DELETE: /api/room-types/{id}
     * Elimina un tipo de habitación existente según su ID.
     * (Método implementado por Fabricio para completar el CRUD de esta entidad en el panel de admin).
     * * @param id El identificador de la categoría que se desea eliminar, extraído de la URL (@PathVariable).
     */
    // Agregado x Fabricio
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        roomTypeService.deleteById(id);
    }
}