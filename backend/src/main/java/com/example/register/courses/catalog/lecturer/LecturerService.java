package com.example.register.courses.catalog.lecturer;

import com.example.register.courses.catalog.lecturer.dto.CreateLecturerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LecturerService {

    private final LecturerRepository lecturerRepository;

    public LecturerService(LecturerRepository lecturerRepository) {
        this.lecturerRepository = lecturerRepository;
    }

    // 1. Thêm giảng viên mới
    @Transactional
    public Lecturer createLecturer(CreateLecturerRequest request) {
        if (lecturerRepository.existsByLecturerCode(request.getLecturerCode())) {
            throw new RuntimeException("Lỗi: Mã giảng viên đã tồn tại!");
        }
        if (lecturerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Lỗi: Email giảng viên đã được sử dụng!");
        }

        Lecturer lecturer = Lecturer.builder()
                .lecturerCode(request.getLecturerCode())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .isPublic(1)
                .build();

        return lecturerRepository.save(lecturer);
    }

    // 2. Lấy tất cả giảng viên (chỉ lấy người dùng active isPublic = 1)
    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findByIsPublic(1);
    }

    // 3. Lấy chi tiết 1 giảng viên theo ID
    public Lecturer getLecturerById(Long id) {
        return lecturerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giảng viên có ID: " + id));
    }

    // 4. Cập nhật thông tin giảng viên
    @Transactional
    public Lecturer updateLecturer(Long id, CreateLecturerRequest request) {
        Lecturer lecturer = getLecturerById(id);

        if (lecturerRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new RuntimeException("Lỗi: Email đã được sử dụng bởi giảng viên khác!");
        }

        lecturer.setFullName(request.getFullName());
        lecturer.setEmail(request.getEmail());
        lecturer.setDepartment(request.getDepartment());

        return lecturerRepository.save(lecturer);
    }

    // 5. Xóa mềm giảng viên (isPublic = 0)
    @Transactional
    public void deleteLecturer(Long id) {
        Lecturer lecturer = getLecturerById(id);
        lecturer.setIsPublic(0);
        lecturerRepository.save(lecturer);
    }

    // 6. Tìm kiếm và phân trang giảng viên
    public Page<Lecturer> searchLecturers(String keyword, String department, Integer isPublic, int page, int size) {
        return lecturerRepository.searchLecturers(
                (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null,
                (department != null && !department.trim().isEmpty()) ? department.trim() : null,
                isPublic,
                PageRequest.of(page, size)
        );
    }

    // 7. Lấy danh sách các Khoa/Bộ môn (Dropdown)
    public List<String> getAllDepartments() {
        return lecturerRepository.findDistinctDepartments();
    }

    // 8. Bật / Tắt trạng thái isPublic
    @Transactional
    public Lecturer toggleLecturerStatus(Long id) {
        Lecturer lecturer = getLecturerById(id);
        lecturer.setIsPublic(lecturer.getIsPublic() == 1 ? 0 : 1);
        return lecturerRepository.save(lecturer);
    }
}