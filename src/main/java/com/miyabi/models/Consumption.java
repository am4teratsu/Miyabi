package com.miyabi.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "consumption")
public class Consumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consumption_id")
    private Integer idConsumption;

    @Column(nullable = false)
    private Integer amount = 1;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(length = 200)
    private String observation;

    @Column(name = "consumption_date", insertable = false, updatable = false)
    private LocalDateTime consumptionDate;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceCatalog service;

    @ManyToOne
    @JoinColumn(name = "user_registration_id")
    private User userRegistration;

    public Consumption() {}
    
    // GETERS AND SETERS

	public Integer getIdConsumption() {
		return idConsumption;
	}

	public void setIdConsumption(Integer idConsumption) {
		this.idConsumption = idConsumption;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public LocalDateTime getConsumptionDate() {
		return consumptionDate;
	}

	public void setConsumptionDate(LocalDateTime consumptionDate) {
		this.consumptionDate = consumptionDate;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public ServiceCatalog getService() {
		return service;
	}

	public void setService(ServiceCatalog service) {
		this.service = service;
	}

	public User getUserRegistration() {
		return userRegistration;
	}

	public void setUserRegistration(User userRegistration) {
		this.userRegistration = userRegistration;
	}
    
    
}