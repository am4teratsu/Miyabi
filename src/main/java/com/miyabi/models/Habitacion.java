package com.miyabi.models;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "habitaciones")
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private Integer idHabitacion;

    @Column(name = "numero_habitacion", nullable = false, unique = true, length = 10)
    private String numeroHabitacion;

    @Column(nullable = false)
    private Integer piso;

    @Column(nullable = false, length = 20)
    private String estado = "Disponible";

    @Column(name = "descripcion_adicional", columnDefinition = "TEXT")
    private String descripcionAdicional;

    @Column(name = "fecha_ultimo_mantenimiento")
    private LocalDate fechaUltimoMantenimiento;

    @ManyToOne
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoHabitacion tipoHabitacion;

    public Habitacion() {}
}