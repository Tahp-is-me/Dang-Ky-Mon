package com.example.register.courses.catalog.semester.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateRegistrationWindowRequest {

    @NotNull(message = "Thời gian mở đăng ký không được để trống")
    private LocalDateTime registrationStart;

    @NotNull(message = "Thời gian đóng đăng ký không được để trống")
    private LocalDateTime registrationEnd;
}