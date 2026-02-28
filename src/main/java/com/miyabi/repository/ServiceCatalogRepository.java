package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.ServiceCatalog;
import java.util.List;

/**
 * Interfaz de Repositorio para la entidad ServiceCatalog.
 * Proporciona el acceso a datos para el catálogo de servicios adicionales 
 * y productos (Room Service, Spa, Lavandería, etc.).
 */
public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Integer> {
    
    /**
     * Query Method: Busca servicios filtrándolos por su estado de disponibilidad.
     * Es utilizado por el recepcionista para cargar únicamente los servicios 
     * que se pueden facturar actualmente al huésped.
     * * @param available Estado de disponibilidad (1 para Activo, 0 para Inactivo).
     * @return Lista de servicios filtrados.
     */
    List<ServiceCatalog> findByAvailable(Integer available);
}