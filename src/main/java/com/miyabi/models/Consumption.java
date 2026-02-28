package com.miyabi.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad (Modelo) que representa la tabla "consumption" en la base de datos.
 * Esta clase registra todos los gastos extra o servicios adicionales (Room Service, Spa, Restaurante)
 * que un huésped consume durante su estancia en el hotel.
 * ¡NOTA PARA LA BOLETA!: De aquí se extrae el "detalle" para imprimir el recibo final.
 */
@Entity // Indica a JPA/Hibernate que esta clase mapea una tabla de la BD.
@Table(name = "consumption") // Nombre exacto de la tabla en SQL Server.
public class Consumption {

    /**
     * Llave primaria (Primary Key) auto-incremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consumption_id")
    private Integer idConsumption;

    /**
     * Cantidad del producto o servicio consumido.
     * Por defecto se asume 1 si no se especifica.
     */
    @Column(nullable = false)
    private Integer amount = 1;

    /**
     * Precio unitario del servicio al momento del consumo.
     * DETALLE TÉCNICO: Se usa BigDecimal en lugar de Double o Float para manejar dinero,
     * ya que evita errores de redondeo con decimales. 
     * precision = 10 (máximo 10 dígitos en total), scale = 2 (2 decimales).
     */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Costo total de este registro (amount * unitPrice).
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /**
     * Notas u observaciones adicionales (Ej. "Sin picante", "Entregar a las 8 PM").
     */
    @Column(length = 200)
    private String observation;

    /**
     * Fecha y hora exacta en la que se registró el consumo.
     * La base de datos (SQL Server) se encarga de asignar este valor automáticamente (CURRENT_TIMESTAMP).
     */
    @Column(name = "consumption_date", insertable = false, updatable = false)
    private LocalDateTime consumptionDate;

    /**
     * Relación de Llave Foránea (Foreign Key) con la Reserva (Reservation).
     * @ManyToOne: "Muchos consumos pertenecen a una sola reserva".
     * 'nullable = false' indica que un consumo no puede existir si no está amarrado a una reserva.
     */
    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    /**
     * Relación de Llave Foránea con el Catálogo de Servicios (ServiceCatalog).
     * @ManyToOne: "Muchos consumos pueden ser del mismo servicio (ej. muchas personas piden Cerveza)".
     */
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceCatalog service;

    /**
     * Relación de Llave Foránea con el Usuario/Empleado (User) que registró el consumo en el sistema.
     * Útil para auditorías (saber qué recepcionista o mozo cargó el gasto a la habitación).
     */
    @ManyToOne
    @JoinColumn(name = "user_registration_id")
    private User userRegistration;

    /**
     * Constructor vacío requerido por JPA.
     */
    public Consumption() {}
    
    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public Integer getIdConsumption() {
        return idConsumption;
    }

    public void setIdConsumption(Integer idConsumption) {
        this.idConsumption = idConsumption;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public LocalDateTime getConsumptionDate() {
        return consumptionDate;
    }

    public void setConsumptionDate(LocalDateTime consumptionDate) {
        this.consumptionDate = consumptionDate;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public ServiceCatalog getService() {
        return service;
    }

    public void setService(ServiceCatalog service) {
        this.service = service;
    }

    public User getUserRegistration() {
        return userRegistration;
    }

    public void setUserRegistration(User userRegistration) {
        this.userRegistration = userRegistration;
    }
}