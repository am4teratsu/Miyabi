package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.miyabi.models.User;
import com.miyabi.repository.UserRepository;

/**
 * Servicio encargado de gestionar la lógica de negocio de los Usuarios (Personal del Hotel).
 * Administra el ciclo de vida de las cuentas del personal administrativo y operativo.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Inyección de dependencias por constructor.
     * Mantiene la arquitectura limpia y facilita las pruebas de integración.
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Recupera la lista completa de empleados registrados en el sistema.
     * Utilizado para el panel de gestión de recursos humanos del hotel.
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Busca un usuario específico por su identificador único.
     */
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Registra un nuevo empleado o actualiza los datos de uno existente.
     * Permite modificar nombres, correos o roles asignados.
     */
    public User save(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Elimina una cuenta de usuario del sistema.
     * (Agregado por Fabricio): Crucial para la revocación de accesos cuando 
     * un empleado deja de laborar en el hotel.
     * @param id Identificador del usuario a eliminar.
     */
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }
}