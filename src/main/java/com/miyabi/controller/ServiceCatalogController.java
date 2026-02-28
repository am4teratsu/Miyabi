package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.ServiceCatalog;
import com.miyabi.service.ServiceCatalogService;

/**
 * Controlador REST para el Catálogo de Servicios del hotel.
 * Gestiona la lista de servicios o productos adicionales que los huéspedes pueden
 * consumir o solicitar (por ejemplo: masajes en el spa, cenas, tours, room service).
 */
@RestController // Indica que esta clase expone una API REST y devuelve las respuestas en formato JSON.
@RequestMapping("/api/services-catalog") // Define la ruta URL base para todos los endpoints del catálogo.
public class ServiceCatalogController {

    // Dependencia del servicio que contiene la lógica de negocio y las consultas a la base de datos.
    private final ServiceCatalogService serviceCatalogService;

    /**
     * Constructor para la Inyección de Dependencias.
     * Spring Boot se encarga de inyectar la instancia de ServiceCatalogService automáticamente al arrancar.
     */
    public ServiceCatalogController(ServiceCatalogService serviceCatalogService) {
        this.serviceCatalogService = serviceCatalogService;
    }

    /**
     * Endpoint GET: /api/services-catalog
     * Obtiene el listado completo de todos los servicios registrados en el hotel, sin importar su estado.
     * Ideal para el panel de administración, donde el gerente necesita revisar el catálogo entero
     * (incluso aquellos servicios que estén deshabilitados o fuera de temporada).
     * @return Lista JSON con todos los objetos de tipo ServiceCatalog.
     */
    @GetMapping
    public List<ServiceCatalog> getAllServices() {
        return serviceCatalogService.findAll();
    }

    /**
     * Endpoint GET: /api/services-catalog/available
     * Obtiene únicamente la lista de servicios que están actualmente habilitados/disponibles.
     * Es fundamental para los recepcionistas o para la web, ya que garantiza que no se le venda
     * ni se le registre a un huésped un consumo (Consumption) de un servicio que temporalmente no se ofrece.
     * @return Lista JSON filtrada con los servicios disponibles.
     */
    @GetMapping("/available")
    public List<ServiceCatalog> getAvailableServices() {
        return serviceCatalogService.findAvailable();
    }

    /**
     * Endpoint POST: /api/services-catalog
     * Permite registrar un nuevo servicio, producto o experiencia en el catálogo del hotel.
     * Se utiliza desde el panel de administrador para actualizar la oferta del negocio.
     * @param serviceCatalog Objeto con los datos del nuevo servicio (nombre, precio, descripción) mapeado desde el JSON recibido (@RequestBody).
     * @return El servicio recién creado y guardado en la base de datos con su ID correspondiente.
     */
    @PostMapping
    public ServiceCatalog createService(@RequestBody ServiceCatalog serviceCatalog) {
        return serviceCatalogService.save(serviceCatalog);
    }
}