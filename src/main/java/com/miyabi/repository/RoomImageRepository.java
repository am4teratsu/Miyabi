package com.miyabi.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.RoomImage;
import java.util.List;

public interface RoomImageRepository extends JpaRepository<RoomImage, Integer> {
    List<RoomImage> findByRoomType_IdTipo(Integer typeId);
}