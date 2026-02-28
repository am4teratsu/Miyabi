package com.miyabi.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Entidad (Modelo) que define las categorías de habitaciones del hotel Miyabi.
 * Aquí se gestiona la información comercial, precios, capacidades y detalles estéticos
 * que se muestran en el catálogo web para el cliente.
 */
@Entity
@Table(name = "room_type")
public class RoomType {
	
    /**
     * MÉTODO DE UTILIDAD (No se guarda en BD):
     * Convierte el String JSON de la base de datos en una Lista de Java.
     * Esto permite que en el frontend (HTML/Thymeleaf) se pueda hacer un "th:each" 
     * para listar los servicios como iconos de forma sencilla.
     */
	@Transient // Indica a JPA que ignore este método, no es una columna de la tabla.
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

    /**
     * Llave primaria auto-incremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer idTipo;

    /**
     * Nombre comercial de la categoría (Ej. "Suite Imperial", "Habitación Tradicional Japonesa").
     */
    @Column(name = "name_type", nullable = false, unique = true, length = 100)
    private String nameType;

    /**
     * Descripción larga y detallada para la página de detalles de la habitación.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Resumen corto para las tarjetas (cards) del listado principal.
     */
    @Column(name = "short_description", length = 255)
    private String shortDescription;

    /**
     * Cantidad máxima de huéspedes permitidos en este tipo de habitación.
     */
    @Column(name = "capacity_people", nullable = false)
    private Integer capacityPeople = 2;

    /**
     * Precio base por noche. Se usa BigDecimal para precisión monetaria.
     */
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    /**
     * Precio especial para temporadas altas (Navidad, Semana Santa, etc.).
     */
    @Column(name = "high_season_price", precision = 10, scale = 2)
    private BigDecimal highSeasonPrice;

    /**
     * URL de la imagen principal (miniatura).
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * URL del croquis o plano arquitectónico de la habitación. 
     * (Detalle de lujo para una web de hotel).
     */
    @Column(name = "floor_plan_url", length = 500)
    private String floorPlanUrl;

    /**
     * Tamaño del cuarto (Ej. "35 m²").
     */
    @Column(name = "room_size", length = 50)
    private String roomSize;

    /**
     * Información de ubicación (Ej. "Frente al jardín Zen", "Piso superior").
     */
    @Column(name = "location_info", length = 150)
    private String locationInfo;

    /**
     * Descripción de la configuración de camas (Ej. "2 camas Queen", "1 King Size").
     */
    @Column(name = "bed_type", length = 150)
    private String bedType;

    /**
     * Almacena una lista de servicios en formato JSON.
     * Ejemplo en BD: ["WiFi", "Minibar", "A/C", "Bata de seda"]
     * 'columnDefinition = "JSON"' es compatible con SQL Server y MySQL modernos.
     */
    @Column(columnDefinition = "JSON")
    private String amenities;

    /**
     * Relación Uno a Muchos con las imágenes de la galería.
     * 'fetch = FetchType.EAGER': Carga las imágenes automáticamente junto con el tipo de cuarto.
     * 'JsonIgnore': Evita bucles infinitos al convertir a JSON para la API.
     */
    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RoomImage> images;

    /**
     * Constructor por defecto.
     */
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