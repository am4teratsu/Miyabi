package com.miyabi.models;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer idRol;

    @Column(name = "name_rol", nullable = false, unique = true, length = 50)
    private String nameRol;

    @Column(length = 200)
    private String description;

    public Rol() {}

    // ==========================================
    // GETTERS Y SETTERS
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