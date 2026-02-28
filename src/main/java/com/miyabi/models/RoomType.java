package com.miyabi.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "room_type")
public class RoomType {
	
	@Transient
    public List<String> getParsedAmenities() {
        if (this.amenities != null && !this.amenities.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(this.amenities, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new java.util.ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer idTipo;

    // Ampliado de 50 a 100
    @Column(name = "name_type", nullable = false, unique = true, length = 100)
    private String nameType;

    @Column(columnDefinition = "TEXT")
    private String description;


    @Column(name = "short_description", length = 255)
    private String shortDescription;

    @Column(name = "capacity_people", nullable = false)
    private Integer capacityPeople = 2;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "high_season_price", precision = 10, scale = 2)
    private BigDecimal highSeasonPrice;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // croquis arquitectónico
    @Column(name = "floor_plan_url", length = 500)
    private String floorPlanUrl;

    //  Tamaño "25 m²"
    @Column(name = "room_size", length = 50)
    private String roomSize;

    //  Ubicación específica,  "Planta 2, vista al jardín"
    @Column(name = "location_info", length = 150)
    private String locationInfo;

    //  Detalle de camas, ej: "1 cama king size + sofá cama"
    @Column(name = "bed_type", length = 150)
    private String bedType;

    // Ahora en formato JSON: ["WiFi","TV","Jacuzzi","Hamaca disponible"]
    @Column(columnDefinition = "JSON")
    private String amenities;

    // Relación con las imágenes del carrusel
    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RoomImage> images;

    public RoomType() {}

   

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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFloorPlanUrl() {
        return floorPlanUrl;
    }

    public void setFloorPlanUrl(String floorPlanUrl) {
        this.floorPlanUrl = floorPlanUrl;
    }

    public String getRoomSize() {
        return roomSize;
    }

    public void setRoomSize(String roomSize) {
        this.roomSize = roomSize;
    }

    public String getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(String locationInfo) {
        this.locationInfo = locationInfo;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public List<RoomImage> getImages() {
        return images;
    }

    public void setImages(List<RoomImage> images) {
        this.images = images;
    }
}