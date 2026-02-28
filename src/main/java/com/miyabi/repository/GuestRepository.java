package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.Guest;
import java.util.Optional;

/**
 * Interfaz de Repositorio para la entidad Guest (Huésped).
 * Proporciona los métodos necesarios para gestionar el acceso a los datos de los clientes.
 */
public interface GuestRepository extends JpaRepository<Guest, Integer> {
    
    /**
     * Busca un huésped utilizando su número de DNI.
     * Es fundamental para el proceso de Check-in manual, permitiendo encontrar 
     * rápidamente al cliente por su documento de identidad oficial.
     * @param dni Documento Nacional de Identidad.
     * @return Un Optional que contiene al Guest si se encuentra, o vacío si no.
     */
    Optional<Guest> findByDni(String dni);
    
    /**
     * Busca un huésped por su dirección de correo electrónico.
     * Este método es vital para el sistema de Autenticación (Login), ya que el email 
     * actúa como el identificador de usuario único en la plataforma web.
     * @param email Correo electrónico del cliente.
     * @return Un Optional con el objeto Guest encontrado.
     */
    Optional<Guest> findByEmail(String email);
}