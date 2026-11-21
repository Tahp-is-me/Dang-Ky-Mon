package com.example.register.courses.auth;

import com.example.register.courses.auth.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    // Xem thông tin cá nhân của chính người đang đăng nhập
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Principal principal) {
        return ResponseEntity.ok(userService.getUserByUsername(principal.getName()));
    }

    // Cập nhật thông tin cá nhân của chính mình (không truyền ID qua URL nữa)
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Principal principal, @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfileByUsername(principal.getName(), request));
    }

    // Đổi mật khẩu của chính mình
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(Principal principal, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePasswordByUsername(principal.getName(), request);
        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }
}