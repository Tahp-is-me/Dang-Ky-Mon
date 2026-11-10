package com.example.register.courses.catalog.lecturer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateLecturerRequest {

    @NotBlank(message = "Mã giảng viên không được để trống")
    @Size(max = 50, message = "Mã giảng viên tối đa 50 ký tự")
    private String lecturerCode;

    @NotBlank(message = "Họ và tên giảng viên không được để trống")
    @Size(max = 100, message = "Họ và tên tối đa 100 ký tự")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 255, message = "Email tối đa 255 ký tự")
    private String email;

    @NotBlank(message = "Khoa/Bộ môn không được để trống")
    @Size(max = 100, message = "Khoa/Bộ môn tối đa 100 ký tự")
    private String department;
}