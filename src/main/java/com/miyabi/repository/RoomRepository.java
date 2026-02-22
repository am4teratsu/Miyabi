package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.Room;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByState(String state);
}