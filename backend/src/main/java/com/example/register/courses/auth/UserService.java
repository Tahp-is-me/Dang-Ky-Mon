package com.example.register.courses.auth;

import com.example.register.courses.auth.dto.*;
import com.example.register.courses.catalog.major.Major;
import com.example.register.courses.catalog.major.MajorRepository;
import com.example.register.courses.catalog.student.Student;
import com.example.register.courses.catalog.student.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MajorRepository majorRepository;
    private final StudentRepository studentRepository;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       MajorRepository majorRepository,
                       StudentRepository studentRepository,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.majorRepository = majorRepository;
        this.studentRepository = studentRepository;
        this.jwtUtils = jwtUtils;
    }

    // 1. Logic Admin tạo Sinh viên mới
    @Transactional
    public User createStudentByAdmin(CreateStudentRequest request) {
        if (userRepository.existsByUsername(request.getStudentCode())) {
            throw new RuntimeException("Lỗi: Mã sinh viên (Username) đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Lỗi: Email đã được sử dụng!");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Lỗi: Số điện thoại đã được sử dụng!");
        }

        Major major = majorRepository.findById(request.getMajorId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Ngành học không tồn tại!"));

        User user = new User();
        user.setUsername(request.getStudentCode());
        user.setPassword(passwordEncoder.encode("123456"));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole("student");
        user.setStatus("active");

        User savedUser = userRepository.save(user);

        Student student = new Student();
        student.setUser(savedUser);
        student.setStudentCode(request.getStudentCode());
        student.setMajor(major);
        student.setAcademicStatus("studying");

        studentRepository.save(student);

        return savedUser;
    }

    // 2. Logic Đăng nhập (Login)
    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        // 1. Sinh chuỗi JWT Token ở đây (dùng thư viện io.jsonwebtoken)
        String jwtToken = jwtUtils.generateJwtToken(user);

        // 2. Trả về JWT Token cùng thông tin User
        return new JwtResponse(jwtToken, "Bearer", user.getId(), user.getUsername(), user.getRole());
    }

    // 3. Logic Refresh Token
    public User refreshToken(RefreshTokenRequest request) {
        // Trả về User theo logic refresh token của bạn
        return userRepository.findByUsername(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh Token không hợp lệ"));
    }

    // 4. Logic Cập nhật Profile
    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        return userRepository.save(user);
    }

    // 5. Logic Đổi mật khẩu
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // 6. Lấy tất cả tài khoản
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 7. Lấy chi tiết 1 tài khoản theo ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có ID: " + id));
    }

    // 8. Cập nhật trạng thái tài khoản (active/locked/inactive)
    @Transactional
    public User updateStatus(Long id, String status) {
        User user = getUserById(id);
        user.setStatus(status);
        return userRepository.save(user);
    }

    // 9. Cập nhật Role tài khoản (admin/student)
    @Transactional
    public User updateRole(Long id, String role) {
        User user = getUserById(id);
        user.setRole(role);
        return userRepository.save(user);
    }
}