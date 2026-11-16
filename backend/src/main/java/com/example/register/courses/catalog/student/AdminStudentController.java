package com.example.register.courses.catalog.student;

import com.example.register.courses.auth.User;
import com.example.register.courses.auth.UserService;
import com.example.register.courses.auth.dto.CreateStudentRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/students")
public class AdminStudentController {

    private final StudentService studentService;
    private final UserService userService;

    public AdminStudentController(StudentService studentService, UserService userService) {
        this.studentService = studentService;
        this.userService = userService;
    }

    // 1. Tạo mới sinh viên (Tạo đồng thời User + Student)
    @PostMapping
    public ResponseEntity<?> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        User newStudent = userService.createStudentByAdmin(request);
        return ResponseEntity.ok(Map.of(
                "message", "Tạo tài khoản sinh viên thành công!",
                "username", newStudent.getUsername(),
                "defaultPassword", "123456"
        ));
    }

    // 2. Lấy danh sách tất cả sinh viên
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // 3. Xem chi tiết sinh viên theo ID
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    // 4. Cập nhật ngành học / trạng thái học tập
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable Long id,
            @RequestParam(required = false) Long majorId,
            @RequestParam(required = false) String academicStatus) {
        return ResponseEntity.ok(studentService.updateStudentByAdmin(id, majorId, academicStatus));
    }

    // 5. Xóa mềm sinh viên (đổi isPublic = 0)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(Map.of("message", "Đã ẩn hồ sơ sinh viên thành công!"));
    }

    // 6. Bật / tắt trạng thái hiển thị sinh viên (isPublic: 1 <-> 0)
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<StudentResponseDTO> toggleStudentStatus(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.toggleStudentStatus(id));
    }
}