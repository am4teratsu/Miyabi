package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.User;
import java.util.Optional;

/**
 * Interfaz de Repositorio para la entidad User (Personal Administrativo).
 * Es el componente principal para la gestión de seguridad y autenticación 
 * de los empleados del hotel en el panel interno.
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * Busca un usuario interno mediante su correo electrónico institucional.
     * Este método es el pilar del proceso de Login para el personal. 
     * * @param email Correo electrónico del empleado.
     * @return Un Optional que contiene al Usuario si las credenciales coinciden, 
     * lo que permite evitar errores de 'NullPointerException' si el correo no existe.
     */
    Optional<User> findByEmail(String email);
}