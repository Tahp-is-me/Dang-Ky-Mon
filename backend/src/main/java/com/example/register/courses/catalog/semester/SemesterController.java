package com.example.register.courses.catalog.semester;

import com.example.register.courses.catalog.semester.dto.CreateSemesterRequest;
import com.example.register.courses.catalog.semester.dto.UpdateRegistrationWindowRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/semesters")
public class SemesterController {

    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    // 1. Tạo học kỳ mới
    @PostMapping
    public ResponseEntity<?> createSemester(@Valid @RequestBody CreateSemesterRequest request) {
        return ResponseEntity.ok(semesterService.createSemester(request));
    }

    // 2. Lấy danh sách tất cả học kỳ (Admin - gồm cả học kỳ đã ẩn)
    @GetMapping
    public ResponseEntity<?> getAllSemesters() {
        return ResponseEntity.ok(semesterService.getAllSemesters());
    }

    // 2.1. Lấy danh sách học kỳ đang công khai (isPublic = 1)
    @GetMapping("/public")
    public ResponseEntity<?> getPublicSemesters() {
        return ResponseEntity.ok(semesterService.getPublicSemesters());
    }

    // 3. Lấy học kỳ hiện hành
    @GetMapping("/active")
    public ResponseEntity<?> getActiveSemester() {
        return ResponseEntity.ok(semesterService.getActiveSemester());
    }

    // 4. Xem chi tiết 1 học kỳ
    @GetMapping("/{id}")
    public ResponseEntity<?> getSemesterById(@PathVariable Long id) {
        return ResponseEntity.ok(semesterService.getSemesterById(id));
    }

    // 5. Cập nhật toàn bộ thông tin học kỳ
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSemester(@PathVariable Long id, @Valid @RequestBody CreateSemesterRequest request) {
        return ResponseEntity.ok(semesterService.updateSemester(id, request));
    }

    // 6. Cập nhật / gia hạn nhanh khung giờ đăng ký tín chỉ
    @PatchMapping("/{id}/registration-window")
    public ResponseEntity<?> updateRegistrationWindow(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateRegistrationWindowRequest request) {
        return ResponseEntity.ok(semesterService.updateRegistrationWindow(id, request));
    }

    // 7. Kích hoạt học kỳ hiện hành
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateSemester(@PathVariable Long id) {
        return ResponseEntity.ok(semesterService.activateSemester(id));
    }

    // 8. Xóa mềm học kỳ (đổi isPublic = 0)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSemester(@PathVariable Long id) {
        semesterService.deleteSemester(id);
        return ResponseEntity.ok(Map.of("message", "Đã ẩn học kỳ thành công!"));
    }

    // 9. Bật / tắt lại trạng thái hiển thị (isPublic: 1 <-> 0)
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleSemesterPublicStatus(@PathVariable Long id) {
        return ResponseEntity.ok(semesterService.toggleSemesterPublicStatus(id));
    }
}