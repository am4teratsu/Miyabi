package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.miyabi.models.Rol;
import com.miyabi.repository.RolRepository;

/**
 * Servicio encargado de gestionar los perfiles de acceso (Roles).
 * Define la lógica de negocio para la clasificación de permisos del personal del hotel.
 */
@Service
public class RolService {

    private final RolRepository rolRepository;

    /**
     * Inyección de dependencias por constructor.
     * Mantiene la arquitectura desacoplada y facilita las pruebas unitarias.
     */
    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    /**
     * Recupera todos los roles configurados en el sistema.
     * Se utiliza principalmente en los formularios de registro de empleados
     * para asignarles un nivel de acceso válido.
     * @return Lista de roles (Ej: ADMIN, RECEPCIONISTA).
     */
    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    /**
     * Permite registrar un nuevo rol o actualizar la descripción de uno existente.
     * @param rol Objeto con el nombre del rol y su descripción de permisos.
     * @return El rol guardado en la base de datos.
     */
    public Rol save(Rol rol) {
        return rolRepository.save(rol);
    }
}