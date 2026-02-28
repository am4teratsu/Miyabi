package com.miyabi.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad (Modelo) que representa la tabla "access_log" en la base de datos.
 * Sirve para llevar un registro de auditoría de quién inicia sesión en el sistema,
 * a qué hora y desde qué dirección IP.
 */
@Entity // Le indica a Spring Boot y a Hibernate que esta clase es una tabla de la base de datos.
@Table(name = "access_log") // Especifica el nombre exacto de la tabla en SQL Server.
public class AccessLog {

    /**
     * Llave primaria de la tabla (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que el ID es auto-incremental (Identity en SQL Server).
    @Column(name = "access_id")
    private Integer idAccess;

    /**
     * Fecha y hora exacta del inicio de sesión.
     * DETALLE TÉCNICO: 'insertable = false, updatable = false' significa que Java no enviará
     * esta fecha; delegamos la responsabilidad a la base de datos para que asigne 
     * el CURRENT_TIMESTAMP o GETDATE() automáticamente al insertar la fila.
     */
    @Column(name = "access_date", insertable = false, updatable = false)
    private LocalDateTime accessDate;

    /**
     * Dirección IP desde la cual el usuario se conectó.
     */
    @Column(name = "access_ip", length = 50)
    private String ipAccess;

    /**
     * Identificador para saber si el que entró es un Empleado ("User") o un Cliente ("Guest").
     */
    @Column(name = "user_type", length = 20)
    private String userType;

    /**
     * Relación de Llave Foránea (Foreign Key) con la tabla de Empleados (User).
     * @ManyToOne indica que "Muchos registros de acceso pueden pertenecer a Un solo empleado".
     */
    @ManyToOne
    @JoinColumn(name = "user_id") // Nombre de la columna física en la tabla access_log
    private User user;

    /**
     * Relación de Llave Foránea (Foreign Key) con la tabla de Clientes (Guest).
     * @ManyToOne indica que "Muchos registros de acceso pueden pertenecer a Un solo huésped".
     */
    @ManyToOne
    @JoinColumn(name = "guest_id") // Nombre de la columna física en la tabla access_log
    private Guest guest;

    /**
     * Constructor vacío requerido obligatoriamente por JPA/Hibernate.
     */
    public AccessLog() {}

    // ==========================================
    // GETTERS Y SETTERS
    // (Métodos de encapsulamiento para acceder y modificar los atributos privados)
    // ==========================================

    public Integer getIdAccess() {
        return idAccess;
    }

    public void setIdAccess(Integer idAccess) {
        this.idAccess = idAccess;
    }

    public LocalDateTime getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(LocalDateTime accessDate) {
        this.accessDate = accessDate;
    }

    public String getIpAccess() {
        return ipAccess;
    }

    public void setIpAccess(String ipAccess) {
        this.ipAccess = ipAccess;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }
}