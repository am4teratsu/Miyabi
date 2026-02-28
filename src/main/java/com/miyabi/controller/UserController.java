package com.miyabi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.User;
import com.miyabi.service.UserService;

/**
 * Controlador REST para la gestión de Usuarios Internos (Users).
 * IMPORTANTE: En este sistema, "User" se refiere al personal del hotel 
 * (administradores, recepcionistas), mientras que "Guest" se refiere a los clientes.
 * Este controlador expone las operaciones CRUD para el panel de administración de empleados.
 */
@RestController // Indica que es una API REST, devuelve datos en formato JSON.
@RequestMapping("/api/users") // Define la ruta base para los endpoints de los empleados del sistema.
public class UserController {

    // Dependencia del servicio que maneja la lógica de negocio y base de datos para los usuarios internos.
    private final UserService userService;

    /**
     * Constructor para la Inyección de Dependencias.
     * Spring Boot proporciona la instancia de UserService de forma automática.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint GET: /api/users
     * Obtiene la lista completa de todo el personal registrado en el sistema.
     * Se utiliza en la vista principal de "Gestión de Usuarios" del panel de administrador.
     * @return Lista JSON con todos los objetos User.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    /**
     * Endpoint GET: /api/users/{id}
     * Busca los datos de un empleado en específico mediante su ID.
     * Ideal para rellenar los formularios de edición en el frontend cuando el administrador 
     * quiere modificar los datos o el rol de alguien del personal.
     * @param id El identificador único del usuario (@PathVariable).
     * @return El objeto User en formato JSON.
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    /**
     * Endpoint POST: /api/users
     * Registra a un nuevo empleado en el sistema (ej. cuando se contrata a un nuevo recepcionista).
     * @param user Objeto mapeado desde el JSON recibido con los datos del nuevo empleado (@RequestBody).
     * @return El usuario recién creado y guardado.
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }
    
    /**
     * Endpoint PUT: /api/users/{id}
     * Permite editar la información, el estado o el rol de un empleado existente.
     * (Implementado por Fabricio).
     * @param id El ID del usuario que se va a modificar.
     * @param userDetails Objeto con los nuevos datos que reemplazarán a los antiguos.
     * @return Un ResponseEntity con el usuario actualizado (HTTP 200) o un error si no se encuentra (HTTP 404).
     */
    // Agregado x Fabricio
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        // 1. Buscamos al usuario en la base de datos
        User user = userService.findById(id);
        
        if (user != null) {
            // 2. Actualizamos los campos básicos y permisos
            user.setNames(userDetails.getNames());
            user.setSurnames(userDetails.getSurnames());
            user.setEmail(userDetails.getEmail());
            user.setState(userDetails.getState()); // Ej. Activo, Inactivo, Suspendido
            user.setRol(userDetails.getRol()); // Ej. Cambio de Recepcionista a Administrador
            
            // 3. Lógica de seguridad:
            // Si el campo de la contraseña viene vacío, significa que el administrador no quiso cambiarla.
            // Solo se sobreescribe si se envió un texto válido, evitando borrar la contraseña actual por accidente.
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(userDetails.getPassword());
            }
            
            // 4. Guardamos los cambios
            return ResponseEntity.ok(userService.save(user));
        }
        // Retorna HTTP 404 Not Found si el ID no existe
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Endpoint DELETE: /api/users/{id}
     * Elimina a un empleado del sistema.
     * (Implementado por Fabricio).
     * @param id El ID del usuario a eliminar.
     */
    // Agregado x Fabricio
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        // Nota técnica: Actualmente hace un borrado físico (DELETE en SQL). 
        // En sistemas reales, a veces se prefiere un "borrado lógico" (cambiar el state a Inactivo) 
        // para mantener el historial de quién registró reservas en el pasado.
        userService.deleteById(id); 
    }
}