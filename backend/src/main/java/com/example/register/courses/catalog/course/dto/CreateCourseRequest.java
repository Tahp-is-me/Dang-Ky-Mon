package com.example.register.courses.catalog.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCourseRequest {

    @NotBlank(message = "Mã môn học không được để trống")
    @Size(max = 50, message = "Mã môn học tối đa 50 ký tự")
    private String courseCode;

    @NotBlank(message = "Tên môn học không được để trống")
    @Size(max = 255, message = "Tên môn học tối đa 255 ký tự")
    private String courseName;

    @NotNull(message = "Số tín chỉ không được để trống")
    @Min(value = 1, message = "Số tín chỉ phải lớn hơn 0")
    private Integer credits;

    private String description;
}