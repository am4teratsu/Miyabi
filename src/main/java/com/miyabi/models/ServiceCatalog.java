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

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer available = 1;

    public ServiceCatalog() {}
}