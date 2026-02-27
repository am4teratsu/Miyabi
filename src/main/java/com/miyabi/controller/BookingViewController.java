package com.miyabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reservation") 
public class BookingViewController {

    @GetMapping("/booking")
    public String showBookingPage() {
        return "reservation/Booking"; 
    }
    
    @GetMapping("/checkout")
    public String showCheckoutPage() {
        return "reservation/Checkout"; 
    }
    
    @GetMapping("/my-reservations")
    public String showMyReservationsPage() {
        return "reservation/MyReservations"; 
    }
}