package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.Rol;
import com.miyabi.service.RolService;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public List<Rol> getAllRoles() {
        return rolService.findAll();
    }

    @PostMapping
    public Rol createRol(@RequestBody Rol rol) {
        return rolService.save(rol);
    }
}