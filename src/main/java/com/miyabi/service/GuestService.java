package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.miyabi.models.Guest;
import com.miyabi.repository.GuestRepository;

/**
 * Servicio encargado de gestionar la lógica de negocio de los Huéspedes.
 * Maneja desde el registro de nuevos clientes hasta la validación de acceso al portal.
 */
@Service
public class GuestService {

    private final GuestRepository guestRepository;

    // Inyección por constructor: asegura que el servicio siempre tenga su repositorio listo.
    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    /**
     * Recupera todos los clientes registrados en el hotel.
     */
    public List<Guest> findAll() {
        return guestRepository.findAll();
    }

    /**
     * Busca un huésped por su ID interno.
     */
    public Guest findById(Integer id) {
        return guestRepository.findById(id).orElse(null);
    }

    /**
     * Lógica de búsqueda por DNI. 
     * Fundamental para evitar registros duplicados durante el flujo de reserva.
     */
    public Guest findByDni(String dni) {
        return guestRepository.findByDni(dni).orElse(null);
    }

    /**
     * Registra o actualiza la información de un huésped.
     */
    public Guest save(Guest guest) {
        return guestRepository.save(guest);
    }
    
    /**
     * LÓGICA DE AUTENTICACIÓN:
     * Verifica las credenciales del cliente para permitirle el acceso a su área privada.
     * @param email Correo electrónico ingresado en el login.
     * @param password Contraseña ingresada.
     * @return El objeto Guest si el acceso es válido, de lo contrario retorna null.
     */
    public Guest authenticate(String email, String password) {
        // Buscamos al huésped por su email único
        Guest guest = guestRepository.findByEmail(email).orElse(null);

        // Validación simple de credenciales (Regla de negocio de acceso)
        if (guest != null && guest.getPassword().equals(password)) {
            return guest;
        }
        return null; 
    }
}