package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.miyabi.models.RoomType;
import com.miyabi.repository.RoomTypeRepository;

/**
 * Servicio encargado de gestionar las categorías o tipos de habitación.
 * Actúa como el motor del catálogo comercial, manejando la información de 
 * precios, capacidades y descripción de servicios (amenities).
 */
@Service
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    /**
     * Inyección de dependencias por constructor.
     */
    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }
    
    /**
     * Busca una categoría específica por su ID.
     * Utilizado para cargar la página de "Detalles de Habitación" y para
     * los cálculos de precio en el motor de reservas.
     */
    public RoomType findById(Integer id) {
        return roomTypeRepository.findById(id).orElse(null);
    }

    /**
     * Recupera todas las categorías registradas (Ej: Suite, Matrimonial, Doble).
     * Alimenta el catálogo principal de la página web.
     */
    public List<RoomType> findAll() {
        return roomTypeRepository.findAll();
    }

    /**
     * Guarda o actualiza una categoría de habitación.
     * Permite al administrador ajustar precios o cambiar descripciones dinámicamente.
     */
    public RoomType save(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }
    
    /**
     * Elimina una categoría de la base de datos.
     * (Agregado por Fabricio): Crucial para la gestión de mantenimiento del catálogo.
     * @param id Identificador de la categoría a eliminar.
     */
    public void deleteById(Integer id) {
        roomTypeRepository.deleteById(id);
    }
}