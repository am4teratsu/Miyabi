package com.miyabi.controller;

import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.miyabi.service.RoomService;
import com.miyabi.service.UserService;
import com.miyabi.service.RoomTypeService;
import com.miyabi.repository.ReservationRepository; 

/**
 * Controlador MVC para el panel de administración (Admin Panel).
 * A diferencia de un RestController, este controlador devuelve vistas (archivos HTML de Thymeleaf)
 * en lugar de datos en formato JSON.
 */
@Controller // Indica que es un controlador MVC de Spring.
@RequestMapping("/admin") // Define el prefijo "/admin" para todas las rutas de esta clase.
public class AdminViewController {

    // Dependencias inyectadas para acceder a la lógica de negocio y base de datos
    private final RoomService roomService;
    private final UserService userService;
    private final RoomTypeService roomTypeService;
    private final ReservationRepository reservationRepository; 

    /**
     * Constructor de la clase para la Inyección de Dependencias.
     * Spring Boot se encarga de instanciar estos servicios y repositorios automáticamente.
     */
    public AdminViewController(RoomService roomService, 
                               UserService userService, 
                               RoomTypeService roomTypeService,
                               ReservationRepository reservationRepository) {
        this.roomService = roomService;
        this.userService = userService;
        this.roomTypeService = roomTypeService;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Endpoint GET: /admin/rooms
     * Carga la vista de gestión de habitaciones.
     * * @param model Objeto de Spring utilizado para pasar variables desde el backend hacia el archivo HTML.
     * @return La ruta del archivo de vista (templates/admin/rooms.html).
     */
    @GetMapping("/rooms")
    public String viewRooms(Model model) {
        // Pasa la lista de todas las habitaciones y categorías al frontend
        model.addAttribute("listRooms", roomService.findAll());
        model.addAttribute("roomTypes", roomTypeService.findAll());
        return "admin/rooms";
    }
    
    /**
     * Endpoint GET: /admin/room-types
     * Carga la vista de categorías/tipos de habitaciones.
     */
    @GetMapping("/room-types")
    public String viewRoomTypes(Model model) {
        model.addAttribute("listTypes", roomTypeService.findAll());
        return "admin/room-types"; 
    }
    
    /**
     * Endpoint GET: /admin/users
     * Carga la vista de gestión de usuarios (recepcionistas, administradores, etc.).
     */
    @GetMapping("/users")
    public String viewUsers(Model model) {
        model.addAttribute("listUsers", userService.findAll());
        return "admin/users"; 
    }
    
    /**
     * Endpoint GET: /admin/dashboard
     * Carga el panel principal (Dashboard) del administrador con estadísticas y métricas clave.
     */
    @GetMapping("/dashboard")
    public String viewDashboard(Model model) {
        // 1. Calcula las ganancias totales sumando los pagos de las reservas
        BigDecimal totalRevenue = reservationRepository.sumTotalRevenue();
        // Si no hay ganancias (null), envía un 0 por defecto para evitar errores en la vista
        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : 0);
        
        // 2. Calcula métricas rápidas: Reservas pendientes, confirmadas y total de habitaciones
        model.addAttribute("pendingCount", reservationRepository.countByState("Pending"));
        model.addAttribute("activeCount", reservationRepository.countByState("Confirmed"));
        model.addAttribute("totalRooms", roomService.findAll().size());
        
        // 3. Obtiene las últimas 5 reservas (ordenadas de forma descendente por ID) para la tabla de actividad reciente
        model.addAttribute("recentReservations", reservationRepository.findTop5ByOrderByReservationIdDesc());
        
        // Retorna la vista templates/admin/dashboard.html con todos los datos inyectados
        return "admin/dashboard"; 
    }
}