package com.example.register.courses.catalog.major;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/majors")
public class MajorController {

    private final MajorService majorService;

    public MajorController(MajorService majorService) {
        this.majorService = majorService;
    }

    @GetMapping
    public ResponseEntity<List<Major>> getAllMajors() {
        return ResponseEntity.ok(majorService.getAllMajors());
    }

    @GetMapping("/public")
    public ResponseEntity<List<Major>> getPublicMajors() {
        return ResponseEntity.ok(majorService.getPublicMajors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Major> getMajorById(@PathVariable Long id) {
        return ResponseEntity.ok(majorService.getMajorById(id));
    }

    @GetMapping("/code/{majorCode}")
    public ResponseEntity<Major> getMajorByCode(@PathVariable String majorCode) {
        return ResponseEntity.ok(majorService.getMajorByCode(majorCode));
    }

    @PostMapping
    public ResponseEntity<Major> createMajor(@RequestBody Major major) {
        return ResponseEntity.ok(majorService.createMajor(major));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Major> updateMajor(@PathVariable Long id, @RequestBody Major major) {
        return ResponseEntity.ok(majorService.updateMajor(id, major));
    }

    // Xóa mềm (ẩn ngành học)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMajor(@PathVariable Long id) {
        majorService.deleteMajor(id);
        return ResponseEntity.ok(Map.of("message", "Đã ẩn ngành học thành công!"));
    }

    // Bật / tắt trạng thái công khai
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Major> toggleMajorStatus(@PathVariable Long id) {
        return ResponseEntity.ok(majorService.toggleMajorStatus(id));
    }
}