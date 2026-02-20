package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.miyabi.models.ServiceCatalog;
import com.miyabi.repository.ServiceCatalogRepository;

@Service
public class ServiceCatalogService {

    private final ServiceCatalogRepository serviceCatalogRepository;

    public ServiceCatalogService(ServiceCatalogRepository serviceCatalogRepository) {
        this.serviceCatalogRepository = serviceCatalogRepository;
    }

    // Listar todos los servicios
    public List<ServiceCatalog> findAll() {
        return serviceCatalogRepository.findAll();
    }

    // Listar solo los que están disponibles para la venta
    public List<ServiceCatalog> findAvailable() {
        return serviceCatalogRepository.findByAvailableTrue();
    }

    // Buscar un servicio por su ID (muy útil para cuando hagamos los Consumos)
    public ServiceCatalog findById(Integer id) {
        return serviceCatalogRepository.findById(id).orElse(null);
    }
}