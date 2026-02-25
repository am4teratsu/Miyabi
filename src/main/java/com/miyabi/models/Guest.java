package com.miyabi.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "guests")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private Integer idGuest;

    @Column(nullable = false, length = 100)
    private String names;

    @Column(nullable = false, length = 100)
    private String surnames;

    @Column(nullable = false, unique = true, length = 15)
    private String dni;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 15)
    private String phone;

    // NUEVO: Teléfono móvil adicional
    @Column(name = "mobile_phone", length = 15)
    private String mobilePhone;

    @Column(length = 200)
    private String address;

    // NUEVO: País
    @Column(nullable = false, length = 100)
    private String country;

    // NUEVO: Ciudad
    @Column(nullable = false, length = 100)
    private String city;

    // NUEVO: Código postal
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "registration_date", insertable = false, updatable = false)
    private LocalDateTime registrationDate;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer state = 1;

    public Guest() {}

    
    public Integer getIdGuest() {
        return idGuest;
    }

    public void setIdCliente(Integer idGuest) {
        this.idGuest = idGuest;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}