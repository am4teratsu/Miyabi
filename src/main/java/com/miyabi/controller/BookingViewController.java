package com.miyabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador MVC encargado de gestionar la navegación (las vistas HTML) 
 * de todo el flujo de reservas para el cliente.
 */
@Controller // Indica que es un controlador MVC; sus métodos devuelven nombres de plantillas HTML (Thymeleaf), no datos JSON.
@RequestMapping("/reservation") // Establece "/reservation" como la ruta base para todos los métodos de esta clase.
public class BookingViewController {

    /**
     * Endpoint GET: /reservation/booking
     * Carga la página principal del motor de reservas donde el cliente busca disponibilidad.
     * * OJO CON LA RUTA: Retorna "pages/Booking". 
     * Según la foto de tus carpetas, tus archivos están en "templates/reservation/Booking.html".
     * Si te da un error 404, deberías cambiar el return a "reservation/Booking".
     */
    @GetMapping("/booking")
    public String showBookingPage() {
        return "pages/Booking"; 
    }
    
    /**
     * Endpoint GET: /reservation/checkout
     * Carga la página del formulario de pago y datos del cliente.
     * * (Misma observación con la ruta: probablemente deba ser "reservation/Checkout").
     */
    @GetMapping("/checkout")
    public String showCheckoutPage() {
        return "pages/Checkout"; 
    }
    
    /**
     * Endpoint GET: /reservation/my-reservations
     * Carga el panel personal donde el huésped puede ver su historial de estadías.
     * * (Misma observación con la ruta: probablemente deba ser "reservation/MyReservations").
     */
    @GetMapping("/my-reservations")
    public String showMyReservationsPage() {
        return "pages/MyReservations"; 
    }
}