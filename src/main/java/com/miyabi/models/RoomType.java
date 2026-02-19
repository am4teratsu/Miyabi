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
}