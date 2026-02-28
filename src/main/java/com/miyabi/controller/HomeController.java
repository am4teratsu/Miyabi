package com.miyabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.miyabi.service.RoomTypeService;

/**
 * Controlador MVC principal (Navegación Pública).
 * Se encarga de gestionar el enrutamiento de las páginas públicas del sitio web.
 * A diferencia de una API REST, este controlador devuelve vistas HTML (plantillas de Thymeleaf).
 */
@Controller 
public class HomeController {
	
    // Dependencia del servicio para acceder a los datos de los tipos de habitaciones
	private final RoomTypeService roomTypeService;
	
    /**
     * Constructor para la Inyección de Dependencias.
     * Spring Boot instancia automáticamente el RoomTypeService.
     */
	public HomeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    /**
     * Endpoint GET: /
     * Ruta raíz. Carga la página principal (Home) del hotel.
     */
    @GetMapping("/")
    public String index() {
        return "pages/Index"; 
    }

    /**
     * Endpoint GET: /facilities
     * Carga la página informativa sobre las instalaciones del hotel (ej. áreas comunes, jardines).
     */
    @GetMapping("/facilities")
    public String facilities() {
        return "pages/Facilities"; 
    }

    /**
     * Endpoint GET: /cuisine
     * Carga la página dedicada a la experiencia gastronómica y restaurante del hotel.
     */
    @GetMapping("/cuisine")
    public String cuisine() {
        return "pages/Cuisine"; 
    }
    
    /**
     * Endpoint GET: /rooms
     * Carga el catálogo público de habitaciones.
     * @param model Objeto de Spring que actúa como puente para pasar datos desde el backend al frontend.
     */
    @GetMapping("/rooms")
    public String rooms(Model model) {
        // Consulta todos los tipos de habitaciones en la base de datos (Japonesa, Zen, etc.)
        // y los envía a la vista HTML bajo el nombre de variable "roomTypes".
        // Thymeleaf usará esta variable para dibujar las tarjetas de habitaciones dinámicamente.
        model.addAttribute("roomTypes", roomTypeService.findAll());
        return "pages/Rooms"; 
    }
    
    /**
     * Endpoint GET: /reservation
     * Carga la página introductoria de reservas (la que contiene las políticas de niños, grupos, etc.).
     */
    @GetMapping("/reservation")
    public String reservation() {
        return "pages/Reservation"; 
    }
    
    /**
     * Endpoint GET: /register
     * Carga el formulario para que un cliente nuevo pueda crear su cuenta.
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "pages/Register"; 
    }
    
    /**
     * Endpoint GET: /profile
     * Carga el panel de control personal del cliente (donde gestionará sus datos y reservas).
     */
    @GetMapping("/profile")
    public String showProfilePage() {
        return "pages/Profile"; 
    }
}