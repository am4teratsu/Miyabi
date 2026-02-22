package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.RoomType;

public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
}