package com.example.register.courses.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repository truy xuất và kiểm tra dữ liệu điểm số.
public interface AcademicResultRepository extends JpaRepository<AcademicResult, Long> {
    // Tìm kết quả theo sinh viên và lớp học phần.
    Optional<AcademicResult> findByStudentIdAndSectionId(Long studentId, Long sectionId);
    // Kiểm tra sinh viên đã được nhập điểm hay chưa.
    boolean existsByStudentIdAndSectionIdAndTotalScoreIsNotNull(Long studentId, Long sectionId);
}
