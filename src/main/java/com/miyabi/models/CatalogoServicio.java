package com.miyabi.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "catalogo_servicios")
public class CatalogoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Integer idServicio;

    @Column(name = "nombre_servicio", nullable = false, length = 100)
    private String nombreServicio;

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(length = 50)
    private String categoria;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer disponible = 1;

    public CatalogoServicio() {}
}