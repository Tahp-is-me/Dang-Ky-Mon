package com.example.register.courses.catalog.course;

import com.example.register.courses.catalog.course.dto.CreateCourseRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // 1. Thêm môn học mới
    @PostMapping
    public ResponseEntity<?> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    // 2. Lấy tất cả môn học
    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // 3. Tìm kiếm và phân trang môn học
    @GetMapping("/search")
    public ResponseEntity<?> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer credits,
            @RequestParam(required = false) Integer isPublic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(courseService.searchCourses(keyword, credits, isPublic, page, size));
    }

    // 4. Xem chi tiết 1 môn học
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // 5. Cập nhật thông tin môn học
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    // 6. Bật / Tắt trạng thái hiển thị (is_public)
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleCourseStatus(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.toggleCourseStatus(id));
    }

    // 7. Xóa (ẩn) môn học
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(Map.of("message", "Xóa (ẩn) môn học thành công!"));
    }
}