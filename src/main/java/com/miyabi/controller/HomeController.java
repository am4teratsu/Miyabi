package com.miyabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index/Index"; 
    }

    @GetMapping("/facilities")
    public String facilities() {
        return "facilities/Facilities"; 
    }

    @GetMapping("/cuisine")
    public String cuisine() {
        return "cuisine/Cuisine"; 
    }
    
    @GetMapping("/reservation")
    public String reservation() {
        return "reservation/Reservation"; 
    }
    
    @GetMapping("/register")
    public String showRegisterPage() {
        return "auth/Register"; 
    }
    
    @GetMapping("/profile")
    public String showProfilePage() {
        return "auth/Profile"; 
    }
}