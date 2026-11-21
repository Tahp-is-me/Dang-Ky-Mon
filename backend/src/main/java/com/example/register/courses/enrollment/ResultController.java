package com.example.register.courses.enrollment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
// Cung cấp REST API xem điểm và quản lý kết quả học tập.
public class ResultController {
    private final AcademicResultService service;
    public ResultController(AcademicResultService service) { this.service = service; }
    public record ScoreRequest(@NotNull @Positive Long studentId, @NotNull @Positive Long sectionId,
        @NotNull @DecimalMin("0.00") @DecimalMax("10.00") @Digits(integer = 2, fraction = 2) BigDecimal totalScore) {}

    // Sinh viên xem kết quả học tập của mình.
    @GetMapping("/api/student/results")
    public Map<String, Object> myResults(Principal principal) {
        return service.getMyResults(principal == null ? null : principal.getName());
    }
    // Admin xem danh sách sinh viên của lớp.
    @GetMapping("/api/admin/sections/{sectionId}/students")
    public List<Map<String, Object>> sectionStudents(@PathVariable("sectionId") Long sectionId) {
        return service.getSectionStudents(sectionId);
    }
    // Admin nhập hoặc cập nhật điểm.
    @PostMapping("/api/admin/academic-results")
    public AcademicResult save(@Valid @RequestBody ScoreRequest request) {
        return service.saveScore(request.studentId(), request.sectionId(), request.totalScore());
    }
    // Admin xem báo cáo học tập của sinh viên.
    @GetMapping("/api/admin/students/{studentId}/report")
    public Map<String, Object> report(@PathVariable("studentId") Long studentId) {
        return service.getStudentReport(studentId);
    }
}
