package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.miyabi.models.AccessLog;
import com.miyabi.repository.AccessLogRepository;

/**
 * Servicio encargado de la lógica de negocio para el registro de accesos.
 * Actúa como intermediario entre la capa de Controladores y la capa de Datos (Repository).
 */
@Service // Indica que esta clase es un componente de servicio gestionado por Spring.
public class AccessLogService {

    // Inyección de dependencias mediante constructor (Práctica recomendada por sobre @Autowired)
    private final AccessLogRepository accessLogRepository;

    public AccessLogService(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    /**
     * Recupera el historial completo de inicios de sesión del sistema.
     * Útil para módulos de seguridad y auditoría de TI.
     * @return Lista de todos los registros de acceso.
     */
    public List<AccessLog> findAll() {
        return accessLogRepository.findAll();
    }

    /**
     * Registra un nuevo evento de acceso en la base de datos.
     * Se llama cada vez que un usuario (Huésped o Empleado) inicia sesión exitosamente.
     * @param accessLog Objeto con los detalles del acceso (usuario, fecha, IP, etc.).
     * @return El registro de acceso guardado.
     */
    public AccessLog save(AccessLog accessLog) {
        return accessLogRepository.save(accessLog);
    }
}