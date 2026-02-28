package com.miyabi.dto;

/**
 * Data Transfer Object (DTO) para los detalles de la boleta/comprobante de pago.
 * Esta clase no representa una tabla en la base de datos; sirve únicamente como 
 * una estructura de transporte de datos.
 * * ¡IMPORTANTE PARA JASPERREPORTS!: 
 * Los nombres de estas variables (cantidad, descripcion, precio, subtotal) 
 * DEBEN coincidir exactamente con los nombres de los campos ($F{cantidad}, etc.) 
 * que diseñaste en tu archivo "boleta_hotel.jrxml".
 */
public class ReceiptDetailDTO {

    // Atributos que formarán cada fila de la tabla en el PDF impreso
    private String cantidad;
    private String descripcion;
    private String precio;
    private String subtotal;

    /**
     * Constructor vacío.
     * Es estrictamente necesario para que librerías internas (como JasperReports o Jackson)
     * puedan instanciar el objeto dinámicamente mediante "Reflexión".
     */
    public ReceiptDetailDTO() {
    }

    /**
     * Constructor con parámetros.
     * Facilita al backend la creación rápida de cada línea del recibo al momento 
     * de leer la base de datos (ej. new ReceiptDetailDTO("1", "Pisco Sour", "25", "25")).
     */
    public ReceiptDetailDTO(String cantidad, String descripcion, String precio, String subtotal) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.precio = precio;
        this.subtotal = subtotal;
    }

    // ── Getters y Setters ──────────────────────────────────────────────────
    // JasperReports utilizará exclusivamente los métodos "get" para extraer 
    // la información de la lista y dibujarla en el PDF.

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }
}