package com.example.register.courses.auth;


import com.example.register.courses.auth.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/admin/students")
    public ResponseEntity<?> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        User newStudent = userService.createStudentByAdmin(request);
        return ResponseEntity.ok(Map.of(
                "message", "Tạo tài khoản sinh viên thành công!",
                "username", newStudent.getUsername(),
                "defaultPassword", "123456"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    // Xem thông tin cá nhân
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Principal principal) {
        // Trong dự án thực tế, principal.getName() trả về username từ JWT token
        // Ví dụ này xử lý mẫu bằng cách lấy user từ username
        return ResponseEntity.ok(principal.getName());
    }

    // Cập nhật thông tin cá nhân (Truyền userId hoặc lấy từ Principal)
    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    // Đổi mật khẩu
    @PutMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }
}