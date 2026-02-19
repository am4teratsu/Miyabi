package com.miyabi.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @Column(name = "codigo_reserva", nullable = false, unique = true, length = 20)
    private String codigoReserva;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "numero_noches", nullable = false)
    private Integer numeroNoches;

    @Column(name = "precio_por_noche", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioPorNoche;

    @Column(name = "subtotal_habitacion", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalHabitacion;

    @Column(name = "total_consumos", precision = 10, scale = 2)
    private BigDecimal totalConsumos = BigDecimal.ZERO;

    @Column(name = "total_pagar", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPagar;

    @Column(nullable = false, length = 20)
    private String estado = "Pendiente";

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_reserva", insertable = false, updatable = false)
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_checkin")
    private LocalDateTime fechaCheckin;

    @Column(name = "fecha_checkout")
    private LocalDateTime fechaCheckout;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_habitacion", nullable = false)
    private Habitacion habitacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario_checkin")
    private Usuario usuarioCheckin;

    @ManyToOne
    @JoinColumn(name = "id_usuario_checkout")
    private Usuario usuarioCheckout;

    public Reserva() {}
}