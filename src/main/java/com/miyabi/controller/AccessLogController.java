package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.AccessLog;
import com.miyabi.service.AccessLogService;

/**
 * Controlador REST para gestionar los registros de acceso (Access Logs) del sistema.
 * Expone los endpoints (API) para consultar y guardar el historial de conexiones.
 */
@RestController // Indica que esta clase es un controlador REST; las respuestas se enviarán automáticamente en formato JSON.
@RequestMapping("/api/access-logs") // Define la ruta URL base para todos los endpoints de esta clase.
public class AccessLogController {

    // Dependencia del servicio que contiene la lógica de negocio de los registros
    private final AccessLogService accessLogService;

    /**
     * Constructor de la clase. 
     * Spring Boot inyecta automáticamente la instancia de AccessLogService aquí (Inyección de Dependencias).
     */
    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    /**
     * Endpoint GET: /api/access-logs
     * Solicita al servicio la lista completa de todos los registros de acceso en la base de datos.
     * * @return Lista de objetos AccessLog en formato JSON.
     */
    @GetMapping
    public List<AccessLog> getAllLogs() {
        return accessLogService.findAll();
    }

    /**
     * Endpoint POST: /api/access-logs
     * Permite registrar un nuevo acceso en el sistema.
     * * @param accessLog Objeto mapeado automáticamente desde el JSON enviado en el cuerpo de la petición HTTP (@RequestBody).
     * @return El registro guardado (con su ID generado por la base de datos).
     */
    @PostMapping
    public AccessLog createLog(@RequestBody AccessLog accessLog) {
        return accessLogService.save(accessLog);
    }
}