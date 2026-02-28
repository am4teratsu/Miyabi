package com.miyabi.models;

import jakarta.persistence.*;

/**
 * Entidad (Modelo) que representa la tabla "roles" en la base de datos.
 * Define los niveles de acceso y permisos para el personal interno del hotel.
 * (Ejemplo: Administrador, Recepcionista, Limpieza, etc.).
 */
@Entity // Indica a JPA/Hibernate que esta clase mapea una tabla de la base de datos.
@Table(name = "roles") // Nombre físico de la tabla en SQL Server.
public class Rol {

    /**
     * Llave primaria (Primary Key) auto-incremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer idRol;

    /**
     * Nombre descriptivo o código del rol (Ej. "ROLE_ADMIN" o "Recepcionista").
     * 'nullable = false': Es obligatorio que tenga un nombre.
     * 'unique = true': Es vital para la seguridad; evita que se creen dos roles con el mismo nombre
     * y confundan al sistema de autenticación al validar permisos.
     */
    @Column(name = "name_rol", nullable = false, unique = true, length = 50)
    private String nameRol;

    /**
     * Breve descripción de las tareas o accesos que permite este rol
     * (Ej. "Acceso total al panel de administración y finanzas").
     */
    @Column(length = 200)
    private String description;

    /**
     * Constructor vacío requerido obligatoriamente por el framework JPA.
     */
    public Rol() {}

    // ==========================================
    // GETTERS Y SETTERS
    // (Métodos para acceder y modificar los atributos de forma segura)
    // ==========================================

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getNameRol() {
        return nameRol;
    }

    public void setNameRol(String nameRol) {
        this.nameRol = nameRol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}