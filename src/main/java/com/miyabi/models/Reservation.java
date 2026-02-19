package com.miyabi.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer idReserva;

    @Column(name = "reservation_code", nullable = false, unique = true, length = 20)
    private String reservationCode;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @Column(name = "number_nights", nullable = false)
    private Integer numberNights;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Column(name = "room_subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal roomSubtotal;

    @Column(name = "total_consumption", precision = 10, scale = 2)
    private BigDecimal totalConsumption = BigDecimal.ZERO;

    @Column(name = "total_pay", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPay;

    @Column(nullable = false, length = 20)
    private String state = "Peding";

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(name = "reservation_date", insertable = false, updatable = false)
    private LocalDateTime reservationDate;

    @Column(name = "chekin_date")
    private LocalDateTime chekinDate;

    @Column(name = "checkout_date")
    private LocalDateTime checkOut;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id_checkin")
    private User userCheckin;

    @ManyToOne
    @JoinColumn(name = "id_usuario_checkout")
    private User userCheckout;

    public Reservation() {}
}