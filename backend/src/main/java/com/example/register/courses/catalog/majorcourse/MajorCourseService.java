package com.example.register.courses.catalog.majorcourse;

import com.example.register.courses.catalog.course.Course;
import com.example.register.courses.catalog.course.CourseRepository;
import com.example.register.courses.catalog.major.Major;
import com.example.register.courses.catalog.major.MajorRepository;
import com.example.register.courses.catalog.majorcourse.dto.AddMajorCourseRequest;
import com.example.register.courses.catalog.majorcourse.dto.UpdateMajorCourseRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MajorCourseService {

    private final MajorCourseRepository majorCourseRepository;
    private final MajorRepository majorRepository;
    private final CourseRepository courseRepository;

    public MajorCourseService(MajorCourseRepository majorCourseRepository,
                              MajorRepository majorRepository,
                              CourseRepository courseRepository) {
        this.majorCourseRepository = majorCourseRepository;
        this.majorRepository = majorRepository;
        this.courseRepository = courseRepository;
    }

    // 1. Thêm môn học vào ngành
    @Transactional
    public MajorCourse addCourseToMajor(AddMajorCourseRequest request) {
        Major major = majorRepository.findById(request.getMajorId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Ngành học!"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Môn học!"));

        if (majorCourseRepository.existsByMajorIdAndCourseId(request.getMajorId(), request.getCourseId())) {
            throw new RuntimeException("Lỗi: Môn học này đã tồn tại trong Ngành!");
        }

        MajorCourse majorCourse = MajorCourse.builder()
                .major(major)
                .course(course)
                .courseType(request.getCourseType() != null ? request.getCourseType() : "mandatory")
                .build();

        return majorCourseRepository.save(majorCourse);
    }

    // 2. Lấy danh sách tất cả môn học thuộc 1 Ngành
    public List<MajorCourse> getCoursesByMajor(Long majorId) {
        if (!majorRepository.existsById(majorId)) {
            throw new RuntimeException("Lỗi: Không tìm thấy Ngành học!");
        }
        return majorCourseRepository.findByMajorId(majorId);
    }

    // 3. Lấy danh sách môn học thuộc Ngành lọc theo loại (mandatory / elective)
    public List<MajorCourse> getCoursesByMajorAndType(Long majorId, String courseType) {
        if (!majorRepository.existsById(majorId)) {
            throw new RuntimeException("Lỗi: Không tìm thấy Ngành học!");
        }
        return majorCourseRepository.findByMajorIdAndCourseType(majorId, courseType);
    }

    // 4. Cập nhật loại môn học (mandatory <-> elective)
    @Transactional
    public MajorCourse updateCourseType(Long id, UpdateMajorCourseRequest request) {
        MajorCourse majorCourse = majorCourseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phân công môn học trong ngành!"));

        majorCourse.setCourseType(request.getCourseType());
        return majorCourseRepository.save(majorCourse);
    }

    // 5. Xóa môn học khỏi Ngành
    @Transactional
    public void removeCourseFromMajor(Long id) {
        if (!majorCourseRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy phân công môn học trong ngành!");
        }
        majorCourseRepository.deleteById(id);
    }
}