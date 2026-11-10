package com.example.register.courses.catalog.student;

import com.example.register.courses.auth.User;
import com.example.register.courses.catalog.major.Major;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @Column(name = "student_code", unique = true, nullable = false)
    private String studentCode;

    @ManyToOne
    @JoinColumn(name = "major_id", referencedColumnName = "id", nullable = false)
    private Major major;

    @Column(name = "academic_status")
    private String academicStatus = "studying";

    @Column(name = "is_public")
    private Integer isPublic = 1;
}