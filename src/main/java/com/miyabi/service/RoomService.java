package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.miyabi.models.Room;
import com.miyabi.repository.RoomRepository;

/**
 * Servicio encargado de la gestión de las habitaciones físicas.
 * Controla el inventario real y el estado operativo de cada cuarto del hotel.
 */
@Service
public class RoomService {

    private final RoomRepository roomRepository;

    /**
     * Inyección por constructor del repositorio de habitaciones.
     */
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Recupera todas las habitaciones registradas, sin importar su estado.
     * Útil para el panel de administración general.
     */
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    /**
     * LÓGICA DE DISPONIBILIDAD:
     * Filtra y retorna únicamente las habitaciones cuyo estado es "Available".
     * Este método es el que alimenta el motor de reservas en el frontend.
     * @return Lista de habitaciones listas para ser ocupadas.
     */
    public List<Room> findAvailableRooms() {
        return roomRepository.findByState("Available");
    }

    /**
     * Busca una habitación específica por su identificador único.
     */
    public Room findById(Integer id) {
        return roomRepository.findById(id).orElse(null);
    }

    /**
     * Guarda o actualiza la información de una habitación.
     * Se utiliza para cambiar el estado de la habitación (Ej: de 'Available' a 'Occupied' 
     * al hacer el check-in).
     */
    public Room save(Room room) {
        return roomRepository.save(room);
    }
}