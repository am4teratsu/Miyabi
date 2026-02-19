package com.miyabi.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_accesos")
public class RegistroAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceso")
    private Integer idAcceso;

    @Column(name = "fecha_acceso", insertable = false, updatable = false)
    private LocalDateTime fechaAcceso;

    @Column(name = "ip_acceso", length = 50)
    private String ipAcceso;

    @Column(name = "tipo_usuario", length = 20)
    private String tipoUsuario;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    public RegistroAcceso() {}
}