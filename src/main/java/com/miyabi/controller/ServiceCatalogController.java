package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.miyabi.models.ServiceCatalog;
import com.miyabi.service.ServiceCatalogService;

@RestController
@RequestMapping("/services") 
public class ServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;

    public ServiceCatalogController(ServiceCatalogService serviceCatalogService) {
        this.serviceCatalogService = serviceCatalogService;
    }

    @GetMapping
    public List<ServiceCatalog> getAllServices() {
        return serviceCatalogService.findAll(); 
    }

    @GetMapping("/available")
    public List<ServiceCatalog> getAvailableServices() {
        return serviceCatalogService.findAvailable();
    }

    @GetMapping("/{id}")
    public ServiceCatalog getServiceById(@PathVariable Integer id) {
        return serviceCatalogService.findById(id);
    }
}