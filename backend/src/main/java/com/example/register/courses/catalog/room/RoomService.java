package com.example.register.courses.catalog.room;

import com.example.register.courses.catalog.room.dto.CreateRoomRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // 1. Thêm phòng học mới
    @Transactional
    public Room createRoom(CreateRoomRequest request) {
        if (roomRepository.existsByRoomCode(request.getRoomCode())) {
            throw new RuntimeException("Lỗi: Mã phòng học đã tồn tại!");
        }

        Room room = Room.builder()
                .roomCode(request.getRoomCode())
                .roomName(request.getRoomName())
                .building(request.getBuilding())
                .capacity(request.getCapacity())
                .isPublic(1)
                .build();

        return roomRepository.save(room);
    }

    // 2. Lấy tất cả phòng học (chỉ lấy isPublic = 1)
    public List<Room> getAllRooms() {
        return roomRepository.findByIsPublic(1);
    }

    // 3. Lấy chi tiết 1 phòng học theo ID
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng học có ID: " + id));
    }

    // 4. Cập nhật thông tin phòng học
    @Transactional
    public Room updateRoom(Long id, CreateRoomRequest request) {
        Room room = getRoomById(id);

        if (!room.getRoomCode().equals(request.getRoomCode())
                && roomRepository.existsByRoomCode(request.getRoomCode())) {
            throw new RuntimeException("Lỗi: Mã phòng học mới đã tồn tại!");
        }

        room.setRoomCode(request.getRoomCode());
        room.setRoomName(request.getRoomName());
        room.setBuilding(request.getBuilding());
        room.setCapacity(request.getCapacity());

        return roomRepository.save(room);
    }

    // 5. Xóa mềm phòng học (isPublic = 0)
    @Transactional
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        room.setIsPublic(0);
        roomRepository.save(room);
    }

    // 6. Tìm kiếm và lọc phòng học
    public Page<Room> searchRooms(String keyword, String building, Integer minCapacity, Integer isPublic, int page, int size) {
        return roomRepository.searchRooms(
                (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null,
                (building != null && !building.trim().isEmpty()) ? building.trim() : null,
                minCapacity,
                isPublic,
                PageRequest.of(page, size)
        );
    }

    // 7. Lấy danh sách các Tòa nhà/Cơ sở
    public List<String> getAllBuildings() {
        return roomRepository.findDistinctBuildings();
    }

    // 8. Bật / Tắt trạng thái hiển thị
    @Transactional
    public Room toggleRoomStatus(Long id) {
        Room room = getRoomById(id);
        room.setIsPublic(room.getIsPublic() == 1 ? 0 : 1);
        return roomRepository.save(room);
    }
}