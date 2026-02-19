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
}