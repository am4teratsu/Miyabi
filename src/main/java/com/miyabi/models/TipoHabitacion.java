package com.miyabi.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tipo_habitacion")
public class TipoHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo")
    private Integer idTipo;

    @Column(name = "nombre_tipo", nullable = false, unique = true, length = 50)
    private String nombreTipo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "capacidad_personas", nullable = false)
    private Integer capacidadPersonas = 2;

    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @Column(name = "precio_temporada_alta", precision = 10, scale = 2)
    private BigDecimal precioTemporadaAlta;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(columnDefinition = "TEXT")
    private String amenidades;

    public TipoHabitacion() {}
}