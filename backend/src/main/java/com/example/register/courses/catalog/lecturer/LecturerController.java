package com.example.register.courses.catalog.lecturer;

import com.example.register.courses.catalog.lecturer.dto.CreateLecturerRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/lecturers")
public class LecturerController {

    private final LecturerService lecturerService;

    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
    }

    @PostMapping
    public ResponseEntity<?> createLecturer(@Valid @RequestBody CreateLecturerRequest request) {
        return ResponseEntity.ok(lecturerService.createLecturer(request));
    }

    @GetMapping
    public ResponseEntity<?> getAllLecturers() {
        return ResponseEntity.ok(lecturerService.getAllLecturers());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchLecturers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer isPublic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(lecturerService.searchLecturers(keyword, department, isPublic, page, size));
    }

    @GetMapping("/departments")
    public ResponseEntity<?> getAllDepartments() {
        return ResponseEntity.ok(lecturerService.getAllDepartments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLecturerById(@PathVariable Long id) {
        return ResponseEntity.ok(lecturerService.getLecturerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLecturer(@PathVariable Long id, @Valid @RequestBody CreateLecturerRequest request) {
        return ResponseEntity.ok(lecturerService.updateLecturer(id, request));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleLecturerStatus(@PathVariable Long id) {
        return ResponseEntity.ok(lecturerService.toggleLecturerStatus(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLecturer(@PathVariable Long id) {
        lecturerService.deleteLecturer(id);
        return ResponseEntity.ok(Map.of("message", "Xóa (ẩn) giảng viên thành công!"));
    }
}