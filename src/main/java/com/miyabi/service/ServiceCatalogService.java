package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.miyabi.models.ServiceCatalog;
import com.miyabi.repository.ServiceCatalogRepository;

/**
 * Servicio encargado de gestionar el catálogo de servicios adicionales.
 * Administra la oferta de productos (Room Service, Tours, Spa) que el hotel 
 * pone a disposición para incrementar sus ingresos por estadía.
 */
@Service
public class ServiceCatalogService {

    private final ServiceCatalogRepository serviceCatalogRepository;

    /**
     * Inyección de dependencias por constructor.
     */
    public ServiceCatalogService(ServiceCatalogRepository serviceCatalogRepository) {
        this.serviceCatalogRepository = serviceCatalogRepository;
    }

    /**
     * Recupera el listado completo de servicios, incluyendo los inactivos.
     * Útil para el administrador que desea reactivar servicios antiguos.
     */
    public List<ServiceCatalog> findAll() {
        return serviceCatalogRepository.findAll();
    }

    /**
     * LÓGICA DE CATÁLOGO:
     * Filtra únicamente los servicios marcados con estado 1 (Activo/Disponible).
     * Es el método que utiliza el recepcionista para mostrar opciones de consumo al huésped.
     * @return Lista de servicios habilitados para la venta.
     */
    public List<ServiceCatalog> findAvailable() {
        return serviceCatalogRepository.findByAvailable(1); // 1 = Activo/Disponible
    }

    /**
     * Busca un servicio específico por su ID.
     */
    public ServiceCatalog findById(Integer id) {
        return serviceCatalogRepository.findById(id).orElse(null);
    }

    /**
     * Guarda o actualiza un servicio en el catálogo.
     * Permite cambiar precios de temporada o descripciones de los servicios.
     */
    public ServiceCatalog save(ServiceCatalog serviceCatalog) {
        return serviceCatalogRepository.save(serviceCatalog);
    }
}