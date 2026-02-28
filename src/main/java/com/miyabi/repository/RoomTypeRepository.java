package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.RoomType;

/**
 * Interfaz de Repositorio para la entidad RoomType (Categorías de Habitación).
 * Gestiona el acceso a los datos de las "plantillas" de habitaciones (Ej. Suite, Estándar).
 * Esta interfaz es fundamental para el catálogo de ventas, ya que permite recuperar
 * los precios base y la capacidad máxima de cada tipo de cuarto.
 */
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
    
    /**
     * Al extender de JpaRepository, tenemos acceso a:
     * - findAll(): Para listar todas las categorías en la página principal de "Habitaciones".
     * - findById(id): Para obtener los detalles específicos (incluyendo el JSON de amenities) 
     * cuando el cliente selecciona una categoría para reservar.
     */
}