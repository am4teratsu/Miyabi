package com.miyabi.models;

import jakarta.persistence.*;

/**
 * Entidad (Modelo) que representa la tabla "room_images" en la base de datos.
 * Maneja la galería de fotos para cada tipo de habitación.
 * Esta estructura permite que el frontend (Thymeleaf/JS) cargue carruseles de imágenes dinámicos.
 */
@Entity
@Table(name = "room_images")
public class RoomImage {

    /**
     * Identificador único de la imagen.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    /**
     * Ruta o URL de la imagen (Ej. "/images/rooms/suite-zen-1.jpg").
     * 'length = 500' asegura espacio suficiente para URLs externas si deciden usar un CDN.
     */
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    /**
     * Texto alternativo para accesibilidad (SEO y lectores de pantalla).
     */
    @Column(name = "alt_text", length = 150)
    private String altText;

    /**
     * Define la prioridad de aparición en la galería (Ej. 1, 2, 3...).
     */
    @Column(name = "display_order")
    private Integer displayOrder = 1;

    /**
     * Bandera para identificar la foto de portada.
     * 1 = Es la imagen principal que sale en las tarjetas del catálogo.
     * 0 = Es una imagen secundaria de la galería.
     */
    @Column(name = "is_main", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isMain = 0;

    /**
     * Relación con la Categoría de Habitación (RoomType).
     * @ManyToOne: "Muchas imágenes pertenecen a un mismo tipo de habitación".
     * Relaciona la foto con la categoría (ej. Suite, Matrimonial) y no con la habitación física 101,
     * así ahorran espacio en la base de datos al no repetir fotos por cada cuarto igual.
     */
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private RoomType roomType;

    /**
     * Constructor vacío requerido por JPA.
     */
    public RoomImage() {}

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
  
    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getIsMain() {
        return isMain;
    }

    public void setIsMain(Integer isMain) {
        this.isMain = isMain;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}