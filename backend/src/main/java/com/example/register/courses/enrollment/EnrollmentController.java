package com.example.register.courses.enrollment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/enrollments")
// Cung cấp REST API đăng ký và hủy môn học cho sinh viên.
public class EnrollmentController {
    private final EnrollmentService service;
    public EnrollmentController(EnrollmentService service) { this.service = service; }
    public record RegisterRequest(@NotNull @Positive Long sectionId) {}

    // Xem các lớp đã đăng ký.
    @GetMapping
    public List<Map<String, Object>> getAll(Principal principal) {
        return service.getMyEnrollments(principal == null ? null : principal.getName());
    }
    // Đăng ký lớp học phần mới.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Enrollment register(Principal principal, @Valid @RequestBody RegisterRequest request) {
        return service.register(principal == null ? null : principal.getName(), request.sectionId());
    }
    // Hủy một lượt đăng ký.
    @DeleteMapping("/{id}")
    public Map<String, String> cancel(Principal principal, @PathVariable("id") Long id) {
        service.cancel(principal == null ? null : principal.getName(), id);
        return Map.of("message", "Hủy đăng ký thành công");
    }
}
