package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.Rol;
import com.miyabi.service.RolService;

/**
 * Controlador REST para gestionar los roles del sistema.
 * Permite administrar los niveles de acceso y permisos de los usuarios internos 
 * del hotel (por ejemplo: Administrador, Recepcionista, Gerente, etc.).
 */
@RestController // Indica que esta clase expone una API REST y las respuestas se envían en formato JSON.
@RequestMapping("/api/roles") // Define la ruta URL base para todos los endpoints de roles.
public class RolController {

    // Dependencia del servicio que contiene la lógica para consultar y guardar roles en la base de datos.
    private final RolService rolService;

    /**
     * Constructor para la Inyección de Dependencias.
     * Spring Boot se encarga de instanciar RolService automáticamente.
     */
    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    /**
     * Endpoint GET: /api/roles
     * Obtiene el catálogo completo de los roles registrados en el sistema.
     * Útil para llenar listas desplegables (selects) cuando se está creando o editando
     * un usuario desde el panel de administración.
     * @return Lista JSON con todos los objetos de tipo Rol.
     */
    @GetMapping
    public List<Rol> getAllRoles() {
        return rolService.findAll();
    }

    /**
     * Endpoint POST: /api/roles
     * Permite registrar un nuevo rol en la base de datos.
     * @param rol Objeto mapeado desde el JSON recibido en la petición HTTP con el nombre y descripción del rol (@RequestBody).
     * @return El rol recién creado y guardado, incluyendo su ID generado.
     */
    @PostMapping
    public Rol createRol(@RequestBody Rol rol) {
        return rolService.save(rol);
    }
}