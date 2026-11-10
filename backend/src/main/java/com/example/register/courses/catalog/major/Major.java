package com.example.register.courses.catalog.major;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "majors")
@Data // <-- Thêm dòng này để tự sinh Getters/Setters
@NoArgsConstructor
@AllArgsConstructor
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "major_code", unique = true, nullable = false)
    private String majorCode;

    @Column(name = "major_name", nullable = false)
    private String majorName;

    private String description;

    @Column(name = "is_public")
    private Integer isPublic = 1;
}