package com.example.register.courses.catalog.student;

import com.example.register.courses.auth.User;
import com.example.register.courses.auth.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/student")
public class StudentProfileController {

    private final StudentService studentService;
    private final UserRepository userRepository;

    public StudentProfileController(StudentService studentService, UserRepository userRepository) {
        this.studentService = studentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<StudentResponseDTO> getMyProfile(Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
        return ResponseEntity.ok(studentService.getStudentByUserId(currentUser.getId()));
    }
}