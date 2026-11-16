package com.example.register.courses.catalog.room;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomCode(String roomCode);

    List<Room> findByIsPublic(Integer isPublic);

    @Query("SELECT DISTINCT r.building FROM Room r WHERE r.building IS NOT NULL AND r.building <> ''")
    List<String> findDistinctBuildings();

    @Query("SELECT r FROM Room r WHERE " +
            "(:keyword IS NULL OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.roomName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:building IS NULL OR r.building = :building) AND " +
            "(:minCapacity IS NULL OR r.capacity >= :minCapacity) AND " +
            "(:isPublic IS NULL OR r.isPublic = :isPublic)")
    Page<Room> searchRooms(@Param("keyword") String keyword,
                           @Param("building") String building,
                           @Param("minCapacity") Integer minCapacity,
                           @Param("isPublic") Integer isPublic,
                           Pageable pageable);
}