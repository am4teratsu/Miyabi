package com.miyabi.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad (Modelo) que representa la tabla "services_catalog" en la base de datos.
 * Define la oferta de productos y servicios adicionales que el hotel pone a disposición
 * de sus huéspedes (Ej: Desayuno Buffet, Sesión de Spa, Lavandería).
 */
@Entity // Indica que esta clase es una entidad persistente de JPA.
@Table(name = "services_catalog") // Mapea a la tabla física en SQL Server.
public class ServiceCatalog {

    /**
     * Identificador único del servicio (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Integer idService;

    /**
     * Nombre comercial del servicio (Ej. "Masaje Shiatsu", "Cena Tradicional").
     */
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    /**
     * Descripción detallada del servicio.
     */
    @Column(length = 200)
    private String description;

    /**
     * Costo del servicio. 
     * Se usa BigDecimal para garantizar precisión decimal en cálculos financieros.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Categoría a la que pertenece (Ej. "Gastronomía", "Bienestar", "Transporte").
     * Ayuda a agrupar los servicios en el frontend.
     */
    @Column(length = 50)
    private String category;
    
    /**
     * Temporada en la que está disponible el servicio. 
     * Por defecto es "All year" (Todo el año).
     */
    @Column(name = "season", nullable = false)
    private String season = "All year";

    /**
     * Estado de disponibilidad (1 = Habilitado, 0 = Deshabilitado).
     * Permite ocultar servicios del catálogo sin borrarlos permanentemente
     * (útil para servicios por temporada o mantenimiento).
     */
    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer available = 1;

    /**
     * Constructor vacío requerido por la especificación JPA.
     */
    public ServiceCatalog() {}

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getIdService() {
        return idService;
    }

    public void setIdService(Integer idService) {
        this.idService = idService;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }
}