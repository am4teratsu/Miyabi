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
    
    public RoomType findById(Integer id) {
        return roomTypeRepository.findById(id).orElse(null);
    }

    public List<RoomType> findAll() {
        return roomTypeRepository.findAll();
    }

    public RoomType save(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }
    
    // Agregado x Fabricio
    public java.util.Optional<RoomType> findById(Integer id) {
        return roomTypeRepository.findById(id);
    }
    
    // Agregado x Fabricio
    public void deleteById(Integer id) {
        roomTypeRepository.deleteById(id);
    }

    
}