package com.example.register.courses.auth;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    @Builder.Default
    @Column(nullable = false)
    private String role = "com/example/register/courses/catalog/student";

    @Builder.Default
    @Column(nullable = false)
    private String status = "active";

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private Integer isPublic = 1;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_token_expiry")
    private LocalDateTime refreshTokenExpiry;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}