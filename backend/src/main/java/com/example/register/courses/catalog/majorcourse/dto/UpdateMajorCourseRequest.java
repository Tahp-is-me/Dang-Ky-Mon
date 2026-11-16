package com.example.register.courses.catalog.majorcourse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateMajorCourseRequest {

    @NotBlank(message = "Loại môn học không được để trống")
    @Pattern(regexp = "^(mandatory|elective)$", message = "Loại môn học phải là 'mandatory' hoặc 'elective'")
    private String courseType;
}