package com.example.register.courses.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.http.HttpMethod;

@Configuration
@EnableMethodSecurity // Cho phép dùng thêm @PreAuthorize nếu muốn phân quyền chi tiết từng hàm
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Chỉ cho phép login và refresh-token không cần đăng nhập
                        .requestMatchers("/api/auth/login", "/api/auth/refresh-token", "/error").permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/admin/courses/**",
                                "/api/admin/lecturers/**",
                                "/api/admin/majors/**",
                                "/api/admin/major-courses/**",
                                "/api/admin/rooms/**",
                                "/api/admin/semesters/**"
                        ).authenticated()

                        // 2. Chỉ ADMIN mới được vào các API /api/admin/**
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 3. Chỉ STUDENT mới được vào các API /api/student/**
                        .requestMatchers("/api/student/**").hasRole("STUDENT")

                        // 4. Tất cả các API còn lại (như /api/auth/me, /profile, /change-password) bắt buộc phải có Token hợp lệ
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}