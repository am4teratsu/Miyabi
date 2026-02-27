package com.miyabi.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "services_catalog")
public class ServiceCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Integer idService;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(length = 200)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 50)
    private String category;
    
    @Column(name = "season", nullable = false)
    private String season = "All year";

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer available = 1;

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