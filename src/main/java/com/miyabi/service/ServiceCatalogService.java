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

    public List<ServiceCatalog> findAll() {
        return serviceCatalogRepository.findAll();
    }

    public List<ServiceCatalog> findAvailable() {
        return serviceCatalogRepository.findByAvailable(1); // 1 = Activo/Disponible
    }

    public ServiceCatalog findById(Integer id) {
        return serviceCatalogRepository.findById(id).orElse(null);
    }

    public ServiceCatalog save(ServiceCatalog serviceCatalog) {
        return serviceCatalogRepository.save(serviceCatalog);
    }
}