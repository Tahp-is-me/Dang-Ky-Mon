package com.example.register.courses.enrollment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "academic_results")
@Getter @Setter @NoArgsConstructor
// Entity lưu điểm và trạng thái kết quả học tập của sinh viên.
public class AcademicResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "section_id")
    private Long sectionId;

    @Column(name = "total_score", precision = 4, scale = 2)
    private BigDecimal totalScore;

    // Các trạng thái kết quả học tập.
    public enum Status { passed, failed, studying, debt }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.studying;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
