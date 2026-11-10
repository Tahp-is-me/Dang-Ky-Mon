package com.example.register.courses.catalog.lecturer;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lecturers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lecturer_code", nullable = false, unique = true, length = 50)
    private String lecturerCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 255)
    private String email;

    @Column(length = 100)
    private String department;

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private Integer isPublic = 1;
}