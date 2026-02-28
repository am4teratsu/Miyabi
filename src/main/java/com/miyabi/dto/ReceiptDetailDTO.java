package com.miyabi.dto;

public class ReceiptDetailDTO {

    private String cantidad;
    private String descripcion;
    private String precio;
    private String subtotal;

    public ReceiptDetailDTO() {
    }

    public ReceiptDetailDTO(String cantidad, String descripcion, String precio, String subtotal) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.precio = precio;
        this.subtotal = subtotal;
    }

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