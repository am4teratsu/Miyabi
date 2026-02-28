package com.miyabi.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad (Modelo) que representa la tabla "payments" en la base de datos.
 * Gestiona el registro de la transacción financiera (el cobro) asociado a una estadía.
 */
@Entity // Define que esta clase es una entidad mapeada a la base de datos.
@Table(name = "payments") // Nombre físico de la tabla en SQL Server.
public class Payments {

    /**
     * Llave primaria auto-incremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer idPayment;

    /**
     * Monto total cobrado al cliente.
     * Como buena práctica financiera, se utiliza BigDecimal para no perder precisión en los centavos.
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Método de pago utilizado (Ej. "Tarjeta de Crédito", "Efectivo", "Transferencia").
     */
    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    /**
     * Estado del pago. Por defecto se inicializa en "Paid" (Pagado), 
     * asumiendo que el registro se crea al momento de confirmar el cobro.
     */
    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "Paid";

    /**
     * Número de comprobante físico o de transacción bancaria (opcional).
     */
    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;

    /**
     * Fecha y hora exacta de la transacción.
     * Al igual que en otras tablas, la base de datos asigna automáticamente el momento exacto (GETDATE()).
     */
    @Column(name = "payment_day", insertable = false, updatable = false)
    private LocalDateTime paymentDay;

    /**
     * Notas adicionales sobre el pago (Ej. "El cliente pagó la mitad en efectivo y la mitad con tarjeta").
     * columnDefinition = "TEXT" permite almacenar textos largos sin límite estricto de caracteres.
     */
    @Column(columnDefinition = "TEXT")
    private String observation;

    /**
     * Relación Uno a Uno (One-To-One) con la tabla Reservation.
     * Regla de negocio: "Una reserva tiene un único registro de pago asociado, y viceversa".
     * 'unique = true' asegura a nivel de base de datos que no se puedan cobrar dos 'Payments' a la misma reserva.
     */
    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    /**
     * Relación de Llave Foránea con el empleado (User) que procesó el cobro.
     * Vital para el cuadre de caja (saber qué recepcionista recibió el dinero).
     */
    @ManyToOne
    @JoinColumn(name = "user_charge_id")
    private User userCharge;

    /**
     * Constructor vacío requerido por JPA.
     */
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