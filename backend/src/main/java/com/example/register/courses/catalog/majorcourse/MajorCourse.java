package com.example.register.courses.catalog.majorcourse;

import com.example.register.courses.catalog.course.Course;
import com.example.register.courses.catalog.major.Major;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "major_courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "major_id", nullable = false)
    private Major major;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Builder.Default
    @Column(name = "course_type", nullable = false)
    private String courseType = "mandatory";
}