package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer> {
}