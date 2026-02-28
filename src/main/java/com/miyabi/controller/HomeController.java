package com.miyabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.miyabi.service.RoomTypeService;


@Controller 
public class HomeController {
	
	private final RoomTypeService roomTypeService;
	
	public HomeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @GetMapping("/")
    public String index() {
        return "pages/Index"; 
    }

    @GetMapping("/facilities")
    public String facilities() {
        return "pages/Facilities"; 
    }

    @GetMapping("/cuisine")
    public String cuisine() {
        return "pages/Cuisine"; 
    }
    
    @GetMapping("/rooms")
    public String rooms(Model model) {
        model.addAttribute("roomTypes", roomTypeService.findAll());
        return "pages/Rooms"; 
    }
    
    @GetMapping("/reservation")
    public String reservation() {
        return "pages/Reservation"; 
    }
    
    @GetMapping("/register")
    public String showRegisterPage() {
        return "pages/Register"; 
    }
    
    @GetMapping("/profile")
    public String showProfilePage() {
        return "pages/Profile"; 
    }
}