package com.miyabi.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * Entidad (Modelo) principal del sistema, representa la tabla "reservations".
 * Centraliza toda la información sobre la estadía de un huésped: fechas esperadas, 
 * precios, quién lo atendió y la habitación asignada.
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    /**
     * Llave primaria (ID de base de datos auto-incremental).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    /**
     * Código público de la reserva (Ej. "RES-2026-8452").
     * Es el código que el cliente recibe por correo y que debe ser único.
     */
    @Column(name = "reservation_code", nullable = false, unique = true, length = 20)
    private String reservationCode;

    /**
     * Fecha de ingreso "planificada" por el cliente al hacer la reserva.
     */
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    /**
     * Fecha de salida "planificada" por el cliente al hacer la reserva.
     */
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    /**
     * Cantidad total de noches calculadas automáticamente (departureDate - entryDate).
     */
    @Column(name = "number_nights", nullable = false)
    private Integer numberNights;

    /**
     * Precio base de la habitación al momento exacto de crear la reserva.
     * (Importante guardarlo aquí por si el hotel cambia sus precios en el futuro).
     */
    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    /**
     * Costo total solo por concepto de habitación (pricePerNight * numberNights).
     */
    @Column(name = "room_subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal roomSubtotal;

    /**
     * Suma de todos los consumos extras (Room Service, Spa). Se inicia en 0.
     */
    @Column(name = "total_consumption", precision = 10, scale = 2)
    private BigDecimal totalConsumption = BigDecimal.ZERO;

    /**
     * Total a pagar final: (roomSubtotal + totalConsumption).
     */
    @Column(name = "total_pay", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPay;

    /**
     * Estado actual de la reserva (Ej. "Pending", "Confirmed", "Cancelled", "Completed").
     */
    @Column(nullable = false, length = 20)
    private String state = "Pending";

    @Column(columnDefinition = "TEXT")
    private String observations;

    // Número de adultos (mínimo 1 por regla de negocio)
    @Column(name = "num_adults", nullable = false)
    private Integer numAdults = 1;

    // Número de niños
    @Column(name = "num_children", nullable = false)
    private Integer numChildren = 0;

    /**
     * Fecha exacta (con hora) en la que el cliente entró a la web y creó este registro.
     * Generada por la BD.
     */
    @Column(name = "reservation_date", insertable = false, updatable = false)
    private LocalDateTime reservationDate;

    /**
     * Fecha y hora "Real" en la que el cliente llegó al hotel y el recepcionista
     * le entregó las llaves físicamente. (Diferente a 'entryDate' que es solo la fecha planificada).
     */
    @Column(name = "checkin_date")
    private LocalDateTime chekinDate;
    
    /**
     * Fecha y hora "Real" en la que el cliente devolvió las llaves y se fue.
     */
    @Column(name = "checkout_date")
    private LocalDateTime checkOut;

    /**
     * Relación con el Cliente (Un huésped puede tener muchas reservas a lo largo de los años).
     */
    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    /**
     * Relación con la Habitación asignada.
     */
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /**
     * Empleado (Usuario) en turno que atendió al cliente a su llegada (Check-In).
     */
    @ManyToOne
    @JoinColumn(name = "user_id_checkin")
    private User userCheckin;

    /**
     * Empleado (Usuario) en turno que le cobró y despidió al cliente (Check-Out).
     */
    @ManyToOne
    @JoinColumn(name = "id_usuario_checkout")
    private User userCheckout;

    public Reservation() {}

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    
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