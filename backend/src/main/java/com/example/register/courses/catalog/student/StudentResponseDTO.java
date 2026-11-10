package com.example.register.courses.catalog.student;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentResponseDTO {
    private Long id;
    private String studentCode;
    private String fullName;
    private String email;
    private String phone;
    private String majorName;
    private String majorCode;
    private String academicStatus;
    private String userStatus;
}