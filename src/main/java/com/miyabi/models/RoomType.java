package com.miyabi.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "room_type")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer idTipo;

    @Column(name = "name_type", nullable = false, unique = true, length = 50)
    private String nameType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "capacity_people", nullable = false)
    private Integer capacityPeople = 2;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "high_season_price", precision = 10, scale = 2)
    private BigDecimal highSeasonPrice;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(columnDefinition = "TEXT")
    private String amenities;

    public RoomType() {}

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public String getNameType() {
        return nameType;
    }

    public void setNameType(String nameType) {
        this.nameType = nameType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacityPeople() {
        return capacityPeople;
    }

    public void setCapacityPeople(Integer capacityPeople) {
        this.capacityPeople = capacityPeople;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getHighSeasonPrice() {
        return highSeasonPrice;
    }

    public void setHighSeasonPrice(BigDecimal highSeasonPrice) {
        this.highSeasonPrice = highSeasonPrice;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }
}