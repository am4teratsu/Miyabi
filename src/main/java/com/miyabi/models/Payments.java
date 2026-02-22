package com.miyabi.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer idPayment;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "Paid";

    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;

    @Column(name = "payment_day", insertable = false, updatable = false)
    private LocalDateTime paymentDay;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "user_charge_id")
    private User userCharge;

    public Payments() {}

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(Integer idPayment) {
        this.idPayment = idPayment;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public LocalDateTime getPaymentDay() {
        return paymentDay;
    }

    public void setPaymentDay(LocalDateTime paymentDay) {
        this.paymentDay = paymentDay;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public User getUserCharge() {
        return userCharge;
    }

    public void setUserCharge(User userCharge) {
        this.userCharge = userCharge;
    }
}