package com.miyabi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.RoomType;
import com.miyabi.service.RoomTypeService;

@RestController
@RequestMapping("/api/room-types")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @GetMapping
    public List<RoomType> getAllRoomTypes() {
        return roomTypeService.findAll();
    }

    @PostMapping
    public RoomType createRoomType(@RequestBody RoomType roomType) {
        return roomTypeService.save(roomType);
    }
}