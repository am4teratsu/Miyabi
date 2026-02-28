package com.miyabi.controller;

import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.miyabi.service.RoomService;
import com.miyabi.service.UserService;
import com.miyabi.service.RoomTypeService;
// 1. IMPORTANTE: Importa el repositorio de reservas
import com.miyabi.repository.ReservationRepository; 

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    // 2. DECLARAR el repositorio o service

    private final RoomService roomService;
    private final UserService userService;
    private final RoomTypeService roomTypeService;
    private final ReservationRepository reservationRepository; 

    // 3. Constructor 
    public AdminViewController(RoomService roomService, 
                               UserService userService, 
                               RoomTypeService roomTypeService,
                               ReservationRepository reservationRepository) {
        this.roomService = roomService;
        this.userService = userService;
        this.roomTypeService = roomTypeService;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/rooms")
    public String viewRooms(Model model) {
        model.addAttribute("listRooms", roomService.findAll());
        model.addAttribute("roomTypes", roomTypeService.findAll());
        return "admin/rooms";
    }
    
    @GetMapping("/room-types")
    public String viewRoomTypes(Model model) {
        model.addAttribute("listTypes", roomTypeService.findAll());
        return "admin/room-types"; 
    }
    
    @GetMapping("/users")
    public String viewUsers(Model model) {
        model.addAttribute("listUsers", userService.findAll());
        return "admin/users"; 
    }
    
    @GetMapping("/dashboard")
    public String viewDashboard(Model model) {
        BigDecimal totalRevenue = reservationRepository.sumTotalRevenue();
        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : 0);
        
        model.addAttribute("pendingCount", reservationRepository.countByState("Pending"));
        model.addAttribute("activeCount", reservationRepository.countByState("Confirmed"));
        model.addAttribute("totalRooms", roomService.findAll().size());
        
        model.addAttribute("recentReservations", reservationRepository.findTop5ByOrderByReservationIdDesc());
        
        return "admin/dashboard"; 
    }
}