package com.example.register.courses.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repository thao tác với bảng đăng ký môn học.
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // Tìm đăng ký thuộc một sinh viên cụ thể.
    Optional<Enrollment> findByIdAndStudentId(Long id, Long studentId);
}
