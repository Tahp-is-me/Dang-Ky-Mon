package com.example.register.courses.catalog.room;

import com.example.register.courses.catalog.room.dto.CreateRoomRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // 1. Thêm phòng học mới
    @PostMapping
    public ResponseEntity<?> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    // 2. Lấy tất cả phòng học
    @GetMapping
    public ResponseEntity<?> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    // 3. Tìm kiếm & Lọc phòng học (kèm sức chứa tối thiểu minCapacity)
    @GetMapping("/search")
    public ResponseEntity<?> searchRooms(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String building,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer isPublic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(roomService.searchRooms(keyword, building, minCapacity, isPublic, page, size));
    }

    // 4. Lấy danh sách Tòa nhà/Cơ sở (Dropdown)
    @GetMapping("/buildings")
    public ResponseEntity<?> getAllBuildings() {
        return ResponseEntity.ok(roomService.getAllBuildings());
    }

    // 5. Xem chi tiết 1 phòng học
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    // 6. Cập nhật thông tin phòng học
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @Valid @RequestBody CreateRoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    // 7. Bật / Tắt trạng thái hiển thị
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleRoomStatus(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.toggleRoomStatus(id));
    }

    // 8. Xóa (ẩn) phòng học
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(Map.of("message", "Xóa (ẩn) phòng học thành công!"));
    }
}