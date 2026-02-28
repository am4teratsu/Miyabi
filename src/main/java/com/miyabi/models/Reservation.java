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
    private Integer reservationId;

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
    private String state = "Pending";

    @Column(columnDefinition = "TEXT")
    private String observations;

    // Número de adultos (mínimo 1)
    @Column(name = "num_adults", nullable = false)
    private Integer numAdults = 1;

    //  Número de niños
    @Column(name = "num_children", nullable = false)
    private Integer numChildren = 0;

    @Column(name = "reservation_date", insertable = false, updatable = false)
    private LocalDateTime reservationDate;

    @Column(name = "checkin_date")
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

    
    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public Integer getNumberNights() {
        return numberNights;
    }

    public void setNumberNights(Integer numberNights) {
        this.numberNights = numberNights;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public BigDecimal getRoomSubtotal() {
        return roomSubtotal;
    }

    public void setRoomSubtotal(BigDecimal roomSubtotal) {
        this.roomSubtotal = roomSubtotal;
    }

    public BigDecimal getTotalConsumption() {
        return totalConsumption;
    }

    public void setTotalConsumption(BigDecimal totalConsumption) {
        this.totalConsumption = totalConsumption;
    }

    public BigDecimal getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(BigDecimal totalPay) {
        this.totalPay = totalPay;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Integer getNumAdults() {
        return numAdults;
    }

    public void setNumAdults(Integer numAdults) {
        this.numAdults = numAdults;
    }

    public Integer getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(Integer numChildren) {
        this.numChildren = numChildren;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalDateTime getChekinDate() {
        return chekinDate;
    }

    public void setChekinDate(LocalDateTime chekinDate) {
        this.chekinDate = chekinDate;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public User getUserCheckin() {
        return userCheckin;
    }

    public void setUserCheckin(User userCheckin) {
        this.userCheckin = userCheckin;
    }

    public User getUserCheckout() {
        return userCheckout;
    }

    public void setUserCheckout(User userCheckout) {
        this.userCheckout = userCheckout;
    }
}