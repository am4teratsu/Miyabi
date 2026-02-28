package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.Rol;

/**
 * Interfaz de Repositorio para la entidad Rol.
 * Proporciona el mecanismo de persistencia para los roles de usuario (Admin, Recepcionista, etc.).
 * * Al heredar de JpaRepository, obtenemos automáticamente las operaciones CRUD 
 * estándar sin necesidad de implementar código adicional o consultas SQL manuales.
 */
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    /**
     * Esta interfaz permite:
     * 1. Consultar el catálogo de roles para asignar permisos a nuevos empleados.
     * 2. Validar la existencia de roles específicos durante la configuración del sistema.
     * 3. Listar roles en formularios de registro de usuarios internos.
     */
}