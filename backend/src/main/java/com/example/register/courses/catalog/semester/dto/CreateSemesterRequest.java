package com.example.register.courses.catalog.semester.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateSemesterRequest {

    @NotBlank(message = "Mã học kỳ không được để trống")
    @Size(max = 50, message = "Mã học kỳ tối đa 50 ký tự")
    private String semesterCode;

    @NotBlank(message = "Tên học kỳ không được để trống")
    @Size(max = 100, message = "Tên học kỳ tối đa 100 ký tự")
    private String semesterName;

    @NotNull(message = "Ngày bắt đầu học kỳ không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc học kỳ không được để trống")
    private LocalDate endDate;

    private LocalDateTime registrationStart;

    private LocalDateTime registrationEnd;

    private Integer isActive = 1;
}