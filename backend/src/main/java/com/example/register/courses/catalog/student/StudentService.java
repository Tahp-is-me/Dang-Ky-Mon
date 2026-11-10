package com.example.register.courses.catalog.student;

import com.example.register.courses.auth.User;
import com.example.register.courses.catalog.major.Major;
import com.example.register.courses.catalog.major.MajorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final MajorRepository majorRepository;

    public StudentService(StudentRepository studentRepository, MajorRepository majorRepository) {
        this.studentRepository = studentRepository;
        this.majorRepository = majorRepository;
    }

    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên có ID: " + id));
        return mapToDTO(student);
    }

    public StudentResponseDTO getStudentByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ sinh viên tương ứng!"));
        return mapToDTO(student);
    }

    @Transactional
    public StudentResponseDTO updateStudentByAdmin(Long studentId, Long majorId, String academicStatus) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên!"));

        if (majorId != null) {
            Major major = majorRepository.findById(majorId)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Ngành học không tồn tại!"));
            student.setMajor(major);
        }

        if (academicStatus != null && !academicStatus.isBlank()) {
            student.setAcademicStatus(academicStatus);
        }

        Student savedStudent = studentRepository.save(student);
        return mapToDTO(savedStudent);
    }

    private StudentResponseDTO mapToDTO(Student student) {
        User user = student.getUser();
        Major major = student.getMajor();
        return StudentResponseDTO.builder()
                .id(student.getId())
                .studentCode(student.getStudentCode())
                .fullName(user != null ? user.getFullName() : null)
                .email(user != null ? user.getEmail() : null)
                .phone(user != null ? user.getPhone() : null)
                .majorName(major != null ? major.getMajorName() : null)
                .majorCode(major != null ? major.getMajorCode() : null)
                .academicStatus(student.getAcademicStatus())
                .userStatus(user != null ? String.valueOf(user.getStatus()) : null) // <-- Sửa tại dòng này
                .build();
    }
}