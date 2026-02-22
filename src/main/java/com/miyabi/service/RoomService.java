package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.miyabi.models.Room;
import com.miyabi.repository.RoomRepository;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<Room> findAvailableRooms() {
        return roomRepository.findByState("Available");
    }

    public Room findById(Integer id) {
        return roomRepository.findById(id).orElse(null);
    }

    public Room save(Room room) {
        return roomRepository.save(room);
    }
}