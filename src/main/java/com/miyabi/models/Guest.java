package com.miyabi.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * Entidad (Modelo) que representa la tabla "guests" (Huéspedes/Clientes) en la base de datos.
 * Almacena los datos personales, de contacto y credenciales de acceso de los clientes del hotel.
 */
@Entity // Indica que esta clase es una entidad gestionada por JPA/Hibernate.
@Table(name = "guests") // Mapea esta clase a la tabla "guests" en SQL Server.
public class Guest {

    /**
     * Llave primaria auto-incremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private Integer idGuest;

    /**
     * Nombres del huésped. 
     * 'nullable = false' asegura a nivel de base de datos que este campo no puede quedar vacío.
     */
    @Column(nullable = false, length = 100)
    private String names;

    @Column(nullable = false, length = 100)
    private String surnames;

    /**
     * Documento de Identidad (DNI, Pasaporte, etc.).
     * 'unique = true' es una restricción vital: impide que se registren dos clientes distintos con el mismo documento.
     */
    @Column(nullable = false, unique = true, length = 15)
    private String dni;

    /**
     * Correo electrónico, utilizado para el inicio de sesión y el envío de la boleta.
     * También debe ser único en toda la base de datos.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Contraseña del cliente para acceder a su portal web.
     * (Nota técnica: En un entorno de producción, este campo almacena el hash de la contraseña, no el texto plano).
     */
    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 15)
    private String phone;

    // NUEVO: Teléfono móvil adicional para contacto rápido (ej. WhatsApp)
    @Column(name = "mobile_phone", length = 15)
    private String mobilePhone;

    @Column(length = 200)
    private String address;

    // NUEVO: País de procedencia (útil para estadísticas de turismo del hotel)
    @Column(nullable = false, length = 100)
    private String country;

    // NUEVO: Ciudad de procedencia
    @Column(nullable = false, length = 100)
    private String city;

    // NUEVO: Código postal
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /**
     * Fecha en la que el cliente creó su cuenta por primera vez.
     * Delegamos la responsabilidad de asignar la fecha al motor de la base de datos.
     */
    @Column(name = "registration_date", insertable = false, updatable = false)
    private LocalDateTime registrationDate;

    /**
     * Estado del cliente (1 = Activo, 0 = Inactivo/Baneado).
     * Excelente práctica de diseño: Permite hacer un "borrado lógico" para no perder 
     * el historial de reservas si el cliente decide eliminar su cuenta.
     */
    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer state = 1;

    /**
     * Constructor vacío obligatorio para JPA.
     */
    public Guest() {}

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    
    public Integer getIdGuest() {
        return idGuest;
    }

    public void setIdGuest(Integer idGuest) {
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