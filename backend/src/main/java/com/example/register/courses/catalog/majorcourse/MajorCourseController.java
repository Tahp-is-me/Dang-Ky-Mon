package com.example.register.courses.catalog.majorcourse;

import com.example.register.courses.catalog.majorcourse.dto.AddMajorCourseRequest;
import com.example.register.courses.catalog.majorcourse.dto.UpdateMajorCourseRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/major-courses")
public class MajorCourseController {

    private final MajorCourseService majorCourseService;

    public MajorCourseController(MajorCourseService majorCourseService) {
        this.majorCourseService = majorCourseService;
    }

    // 1. Gán môn học vào ngành
    @PostMapping
    public ResponseEntity<?> addCourseToMajor(@Valid @RequestBody AddMajorCourseRequest request) {
        return ResponseEntity.ok(majorCourseService.addCourseToMajor(request));
    }

    // 2. Lấy danh sách môn học thuộc 1 ngành (có thể lọc theo courseType nếu có query param)
    @GetMapping("/major/{majorId}")
    public ResponseEntity<?> getCoursesByMajor(
            @PathVariable Long majorId,
            @RequestParam(required = false) String courseType) {
        if (courseType != null && !courseType.trim().isEmpty()) {
            return ResponseEntity.ok(majorCourseService.getCoursesByMajorAndType(majorId, courseType.trim()));
        }
        return ResponseEntity.ok(majorCourseService.getCoursesByMajor(majorId));
    }

    // 3. Cập nhật loại môn (mandatory / elective)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourseType(@PathVariable Long id, @Valid @RequestBody UpdateMajorCourseRequest request) {
        return ResponseEntity.ok(majorCourseService.updateCourseType(id, request));
    }

    // 4. Xóa môn học khỏi ngành
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeCourseFromMajor(@PathVariable Long id) {
        majorCourseService.removeCourseFromMajor(id);
        return ResponseEntity.ok(Map.of("message", "Xóa môn học khỏi ngành thành công!"));
    }
}