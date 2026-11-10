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

    @GetMapping("/{id}")
    public ResponseEntity<Major> getMajorById(@PathVariable Long id) {
        return ResponseEntity.ok(majorService.getMajorById(id));
    }

    @PostMapping
    public ResponseEntity<Major> createMajor(@RequestBody Major major) {
        return ResponseEntity.ok(majorService.createMajor(major));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Major> updateMajor(@PathVariable Long id, @RequestBody Major major) {
        return ResponseEntity.ok(majorService.updateMajor(id, major));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMajor(@PathVariable Long id) {
        majorService.deleteMajor(id);
        return ResponseEntity.ok(Map.of("message", "Xóa ngành học thành công!"));
    }
}