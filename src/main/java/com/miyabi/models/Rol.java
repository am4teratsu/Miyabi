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
}