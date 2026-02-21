package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.Guest;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Integer> {
    
    Optional<Guest> findByDni(String dni);
    
    Optional<Guest> findByEmail(String email);
}