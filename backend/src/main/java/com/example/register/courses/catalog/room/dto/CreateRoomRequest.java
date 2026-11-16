package com.example.register.courses.catalog.room.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRoomRequest {

    @NotBlank(message = "Mã phòng học không được để trống")
    @Size(max = 50, message = "Mã phòng học tối đa 50 ký tự")
    private String roomCode;

    @NotBlank(message = "Tên phòng học không được để trống")
    @Size(max = 100, message = "Tên phòng học tối đa 100 ký tự")
    private String roomName;

    @NotBlank(message = "Tòa nhà/Cơ sở không được để trống")
    @Size(max = 100, message = "Tòa nhà/Cơ sở tối đa 100 ký tự")
    private String building;

    @NotNull(message = "Sức chứa phòng không được để trống")
    @Min(value = 1, message = "Sức chứa phòng phải lớn hơn 0")
    private Integer capacity;
}