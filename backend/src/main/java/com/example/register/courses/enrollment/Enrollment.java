package com.example.register.courses.enrollment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Getter @Setter @NoArgsConstructor
// Entity lưu thông tin đăng ký lớp học phần của sinh viên.
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "section_id", nullable = false)
    private Long sectionId;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    // Các trạng thái có thể có của một lượt đăng ký.
    public enum Status { registered, cancelled, completed }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.registered;
}
