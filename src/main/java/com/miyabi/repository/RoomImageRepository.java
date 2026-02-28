package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.RoomImage;
import java.util.List;

/**
 * Interfaz de Repositorio para la entidad RoomImage.
 * Encargada de gestionar la persistencia y recuperación de las rutas de imágenes
 * que conforman las galerías de fotos de cada tipo de habitación.
 */
public interface RoomImageRepository extends JpaRepository<RoomImage, Integer> {
    
    /**
     * Query Method: Recupera la lista de imágenes asociadas a una categoría de habitación.
     * Es utilizado por el controlador de la web para cargar dinámicamente el carrusel 
     * de fotos cuando un cliente hace clic en "Ver detalles" de una habitación.
     * * @param typeId El ID de la categoría (RoomType) a la que pertenecen las fotos.
     * @return Una lista de objetos RoomImage con las URLs y textos descriptivos.
     */
    List<RoomImage> findByRoomType_IdTipo(Integer typeId);
}