package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.Room;
import com.miyabi.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.findAll();
    }

    @GetMapping("/available")
    public List<Room> getAvailableRooms() {
        return roomService.findAvailableRooms();
    }

    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable Integer id) {
        return roomService.findById(id);
    }

    @PostMapping
    public Room createRoom(@RequestBody Room room) {
        return roomService.save(room);
    }
}