package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.AccessLog;

/**
 * Interfaz de Repositorio para la entidad AccessLog.
 * Al extender de JpaRepository, Spring Data JPA genera automáticamente 
 * toda la implementación necesaria para interactuar con SQL Server.
 * * Beneficio técnico: No necesitamos escribir consultas SQL manuales (SELECT, INSERT, UPDATE) 
 * para las operaciones básicas, lo que reduce el error humano y acelera el desarrollo.
 */
public interface AccessLogRepository extends JpaRepository<AccessLog, Integer> {
    
    /**
     * Al heredar de JpaRepository<AccessLog, Integer>, ya tenemos disponibles métodos como:
     * - save(accessLog): Para registrar un nuevo inicio de sesión.
     * - findAll(): Para obtener el historial completo de accesos (auditoría).
     * - findById(id): Para buscar un registro específico por su ID.
     */
}