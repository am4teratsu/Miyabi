package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.miyabi.models.RoomType;
import com.miyabi.repository.RoomTypeRepository;

@Service
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    public List<RoomType> findAll() {
        return roomTypeRepository.findAll();
    }

    public RoomType save(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }
}