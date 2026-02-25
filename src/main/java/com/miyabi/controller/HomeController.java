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

    @GetMapping("/facilities")
    public String facilities() {
        return "facilities"; 
    }

    @GetMapping("/cuisine")
    public String cuisine() {
        return "cuisine"; 
    }
}