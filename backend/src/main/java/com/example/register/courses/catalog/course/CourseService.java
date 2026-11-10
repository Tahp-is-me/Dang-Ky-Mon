package com.example.register.courses.catalog.course;

import com.example.register.courses.catalog.course.dto.CreateCourseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // 1. Thêm môn học mới
    @Transactional
    public Course createCourse(CreateCourseRequest request) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new RuntimeException("Lỗi: Mã môn học đã tồn tại!");
        }

        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .courseName(request.getCourseName())
                .credits(request.getCredits())
                .description(request.getDescription())
                .isPublic(1)
                .build();

        return courseRepository.save(course);
    }

    // 2. Lấy tất cả môn học (chỉ lấy isPublic = 1)
    public List<Course> getAllCourses() {
        return courseRepository.findByIsPublic(1);
    }

    // 3. Lấy chi tiết 1 môn học theo ID
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học có ID: " + id));
    }

    // 4. Cập nhật thông tin môn học
    @Transactional
    public Course updateCourse(Long id, CreateCourseRequest request) {
        Course course = getCourseById(id);

        course.setCourseName(request.getCourseName());
        course.setCredits(request.getCredits());
        course.setDescription(request.getDescription());

        return courseRepository.save(course);
    }

    // 5. Xóa mềm môn học (isPublic = 0)
    @Transactional
    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        course.setIsPublic(0);
        courseRepository.save(course);
    }

    // 6. Tìm kiếm và phân trang
    public Page<Course> searchCourses(String keyword, Integer credits, Integer isPublic, int page, int size) {
        return courseRepository.searchCourses(
                (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null,
                credits,
                isPublic,
                PageRequest.of(page, size)
        );
    }

    // 7. Bật / Tắt trạng thái isPublic
    @Transactional
    public Course toggleCourseStatus(Long id) {
        Course course = getCourseById(id);
        course.setIsPublic(course.getIsPublic() == 1 ? 0 : 1);
        return courseRepository.save(course);
    }
}