package com.miyabi.models;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer idRoom;

    @Column(name = "room_number", nullable = false, unique = true, length = 10)
    private String roomNumber;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false, length = 20)
    private String state = "Available";

    @Column(name = "additional_description", columnDefinition = "TEXT")
    private String additionalDescription;

    @Column(name = "date_last_maintenance")
    private LocalDate dateLasMaintenance;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private RoomType roomType;

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