package com.example.register.courses.catalog.majorcourse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddMajorCourseRequest {

    @NotNull(message = "Vui lòng chọn Ngành học")
    private Long majorId;

    @NotNull(message = "Vui lòng chọn Môn học")
    private Long courseId;

    @Pattern(regexp = "^(mandatory|elective)$", message = "Loại môn học phải là 'mandatory' hoặc 'elective'")
    private String courseType = "mandatory";
}