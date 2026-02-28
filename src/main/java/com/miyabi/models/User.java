package com.miyabi.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * Entidad (Modelo) que representa la tabla "users" en la base de datos.
 * Define a los usuarios internos del sistema (Administradores, Recepcionistas, etc.).
 * Nota: No confundir con "Guest", que representa a los clientes finales.
 */
@Entity // Indica a JPA que esta clase debe mapearse como una tabla en la BD.
@Table(name = "users") // Nombre físico de la tabla en SQL Server.
public class User {

    /**
     * Llave primaria (Primary Key) auto-incremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer idUsuario;

    /**
     * Nombres del empleado.
     */
    @Column(nullable = false, length = 100)
    private String names;

    /**
     * Apellidos del empleado.
     */
    @Column(nullable = false, length = 100)
    private String surnames;

    /**
     * Correo electrónico institucional, sirve como nombre de usuario para el login.
     * 'unique = true' garantiza que no existan dos cuentas con el mismo correo.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Contraseña encriptada del usuario.
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Estado del usuario (1 = Activo, 0 = Inactivo).
     * Permite deshabilitar el acceso a un empleado sin borrar sus datos históricos.
     */
    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer state = 1;

    /**
     * Fecha y hora en la que se creó la cuenta del empleado.
     * 'insertable = false, updatable = false' indica que el valor lo pone la BD (GETDATE()).
     */
    @Column(name = "creation_date", insertable = false, updatable = false)
    private LocalDateTime creationDate;

    /**
     * Relación de Llave Foránea con la tabla de Roles.
     * @ManyToOne: "Muchos usuarios pueden compartir el mismo Rol (ej. varios recepcionistas)".
     * 'nullable = false': Todo usuario interno debe tener un rol asignado obligatoriamente.
     */
    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    /**
     * Constructor vacío requerido por la especificación de Java Persistence API (JPA).
     */
    public User() {}

    // ==========================================
    // GETTERS Y SETTERS
    // (Métodos para acceder y modificar la información de forma controlada)
    // ==========================================

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}