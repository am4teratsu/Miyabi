package com.miyabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class HomeController {

    // Ruta para la página principal 
    @GetMapping("/")
    public String index() {
        return "index"; 
    }

    @GetMapping("/habitaciones")
    public String suites() {
        return "suites"; 
    }

    @GetMapping("/reservas")
    public String booking() {
        return "booking"; 
    }
}