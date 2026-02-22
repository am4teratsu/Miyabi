package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.miyabi.models.Rol;
import com.miyabi.repository.RolRepository;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    public Rol save(Rol rol) {
        return rolRepository.save(rol);
    }
}