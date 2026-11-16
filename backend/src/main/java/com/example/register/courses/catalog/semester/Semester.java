package com.example.register.courses.catalog.semester;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "semesters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "semester_code", nullable = false, unique = true, length = 50)
    private String semesterCode;

    @Column(name = "semester_name", nullable = false, length = 100)
    private String semesterName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "registration_start")
    private LocalDateTime registrationStart;

    @Column(name = "registration_end")
    private LocalDateTime registrationEnd;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Integer isActive = 1;

    // Thuộc tính tính toán tự động trả về JSON: Cho biết cổng đăng ký hiện có đang mở hay không
    @Transient
    public boolean isRegistrationOpen() {
        if (isActive == null || isActive != 1 || registrationStart == null || registrationEnd == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(registrationStart) && !now.isAfter(registrationEnd);
    }
}