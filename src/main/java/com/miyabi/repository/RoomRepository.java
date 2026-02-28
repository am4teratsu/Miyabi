package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.Room;
import java.util.List;

/**
 * Interfaz de Repositorio para la entidad Room (Habitaciones físicas).
 * Proporciona el acceso a datos para gestionar el inventario de cuartos del hotel.
 */
public interface RoomRepository extends JpaRepository<Room, Integer> {
    
    /**
     * Query Method: Filtra las habitaciones según su estado operativo.
     * Es utilizado principalmente por el motor de búsqueda para mostrar solo las 
     * habitaciones con estado "Available" (Disponible) a los clientes.
     * También permite al personal de limpieza listar habitaciones en estado "Maintenance".
     * * @param state El estado a consultar (ej. "Available", "Occupied", "Maintenance").
     * @return Lista de habitaciones que coinciden con dicho estado.
     */
    List<Room> findByState(String state);
}