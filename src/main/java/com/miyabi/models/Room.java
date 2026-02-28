package com.miyabi.models;

import java.time.LocalDate;
import jakarta.persistence.*;

/**
 * Entidad (Modelo) que representa la tabla "rooms" (Habitaciones físicas) en la base de datos.
 * Esta clase maneja el inventario real del hotel (Ej. Habitación 101, 102, 205).
 * ¡OJO!: No guarda el precio ni la capacidad; eso se delega a la relación con "RoomType" 
 * (Excelente aplicación de las reglas de normalización de bases de datos).
 */
@Entity // Indica a JPA/Hibernate que esta clase es una tabla.
@Table(name = "rooms") // Mapea exactamente con el nombre de la tabla en SQL Server.
public class Room {

    /**
     * Llave primaria (Primary Key) auto-incremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer idRoom;

    /**
     * Número o identificador físico de la puerta (Ej. "101A", "305").
     * 'unique = true': Restricción vital que evita que existan dos habitaciones con el mismo número en el hotel.
     */
    @Column(name = "room_number", nullable = false, unique = true, length = 10)
    private String roomNumber;

    /**
     * Piso en el que se encuentra la habitación.
     * Útil para que recepción asigne habitaciones cercanas a personas mayores, por ejemplo.
     */
    @Column(nullable = false)
    private Integer floor;

    /**
     * Estado operativo actual de la habitación. 
     * Por defecto se inicializa en "Available" (Disponible). 
     * Otros estados podrían ser "Occupied" (Ocupada) o "Maintenance" (En mantenimiento).
     * El motor de reservas (ReservationController) filtra las habitaciones basándose en este campo.
     */
    @Column(nullable = false, length = 20)
    private String state = "Available";

    /**
     * Descripción adicional o notas específicas de esta habitación en particular.
     * (Ej. "Tiene vista directa a la piscina", "El aire acondicionado hace un poco de ruido").
     * 'columnDefinition = "TEXT"' permite textos más largos que un simple VARCHAR.
     */
    @Column(name = "additional_description", columnDefinition = "TEXT")
    private String additionalDescription;

    /**
     * Fecha en la que se le hizo el último mantenimiento profundo a la habitación.
     * Un dato excelente para el módulo de operaciones/limpieza del hotel.
     * (Nota técnica: el atributo se llama dateLasMaintenance, faltó la 't' de Last, 
     * pero funciona perfecto siempre y cuando se use ese mismo nombre en el frontend).
     */
    @Column(name = "date_last_maintenance")
    private LocalDate dateLasMaintenance;

    /**
     * Relación de Llave Foránea con la tabla de Categorías (RoomType).
     * @ManyToOne: "Muchas habitaciones físicas pueden pertenecer a una misma categoría (Ej. 10 habitaciones son Suite)".
     * 'nullable = false': Una habitación no puede existir en el sistema si no se le asigna una categoría.
     */
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private RoomType roomType;

    /**
     * Constructor vacío obligatorio para JPA.
     */
    public Room() {}

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(Integer idRoom) {
        this.idRoom = idRoom;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAdditionalDescription() {
        return additionalDescription;
    }

    public void setAdditionalDescription(String additionalDescription) {
        this.additionalDescription = additionalDescription;
    }

    public LocalDate getDateLasMaintenance() {
        return dateLasMaintenance;
    }

    public void setDateLasMaintenance(LocalDate dateLasMaintenance) {
        this.dateLasMaintenance = dateLasMaintenance;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}