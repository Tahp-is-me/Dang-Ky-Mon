package com.example.register.courses.enrollment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
// Xử lý nghiệp vụ đăng ký, hủy đăng ký và kiểm tra điều kiện của lớp.
public class EnrollmentService {
    private final EnrollmentRepository repository;
    private final AcademicResultRepository results;
    private final Sql queries;

    public EnrollmentService(EnrollmentRepository repository, AcademicResultRepository results, JdbcTemplate jdbc,
                             @Value("${app.enrollment.require-registration-flag:true}") boolean requireRegistrationFlag) {
        this.repository = repository;
        this.results = results;
        this.queries = new Sql(jdbc, requireRegistrationFlag);
    }

    // Lấy danh sách lớp sinh viên đã đăng ký.
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMyEnrollments(String username) {
        var student = queries.studentByUsername(username, false);
        return queries.currentEnrollments(student.id());
    }

    // Đăng ký lớp: kiểm tra điều kiện, lưu đăng ký và tăng sĩ số.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Enrollment register(String username, Long sectionId) {
        // Luôn khóa sinh viên trước, sau đó lớp. Hai request cùng SV phải chạy nối tiếp.
        var student = queries.studentByUsername(username, true);
        var section = queries.section(sectionId, true); // SELECT ... FOR UPDATE
        if (!queries.semester(section.semesterId()).open())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Học kỳ chưa mở hoặc đã đóng đăng ký");
        if (!"open".equals(section.status()) || !Integer.valueOf(1).equals(section.isPublic()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lớp không mở đăng ký");
        if (!queries.inMajor(student.majorId(), section.courseId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Môn không thuộc chương trình đào tạo của sinh viên");
        var missing = queries.missingPrerequisites(student.id(), section.courseId());
        if (!missing.isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chưa hoàn thành môn tiên quyết: " + missing);
        if (queries.duplicateCourse(student.id(), section))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đã đăng ký môn này trong học kỳ");
        if (section.current() == null || section.capacity() == null || section.current() < 0 || section.current() >= section.capacity())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lớp đã hết chỗ hoặc sĩ số không hợp lệ");
        if (queries.overlaps(student.id(), section))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Trùng lịch học với lớp đã đăng ký");
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(student.id());
        enrollment.setSectionId(section.id());
        enrollment.setRegisteredAt(LocalDateTime.now());
        enrollment.setStatus(Enrollment.Status.registered);
        Enrollment saved = repository.saveAndFlush(enrollment);
        queries.increment(section.id());
        return saved;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    // Hủy đăng ký hợp lệ và giảm sĩ số; giữ lại lịch sử đăng ký.
    public void cancel(String username, Long enrollmentId) {
        var student = queries.studentByUsername(username, true);
        var enrollment = repository.findByIdAndStudentId(enrollmentId, student.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đăng ký của bạn"));
        var section = queries.section(enrollment.getSectionId(), true);
        if (!queries.semester(section.semesterId()).open())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Học kỳ đã đóng cổng đăng ký/hủy");
        if (enrollment.getStatus() != Enrollment.Status.registered)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đăng ký đã hủy hoặc đã hoàn thành");
        if (results.existsByStudentIdAndSectionIdAndTotalScoreIsNotNull(student.id(), section.id()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể hủy lớp đã có điểm");
        enrollment.setStatus(Enrollment.Status.cancelled);
        repository.saveAndFlush(enrollment);
        queries.decrement(section.id());
    }


    // Các truy vấn SQL phục vụ kiểm tra đăng ký và cập nhật sĩ số.
    static final class Sql {
        private final JdbcTemplate jdbc;
        private final boolean requireRegistrationFlag;

        Sql(JdbcTemplate jdbc, boolean requireRegistrationFlag) {
            this.jdbc = jdbc;
            this.requireRegistrationFlag = requireRegistrationFlag;
        }

    record StudentInfo(Long id, Long majorId) {}
    record SectionInfo(Long id, Long courseId, Long semesterId, Integer current, Integer capacity, String status, Integer isPublic) {}
    record SemesterInfo(Integer active, Integer isPublic, boolean registrationFlag,
                        LocalDateTime start, LocalDateTime end, boolean requireFlag) {
        boolean open() {
            LocalDateTime now = LocalDateTime.now();
            return Integer.valueOf(1).equals(active) && Integer.valueOf(1).equals(isPublic)
                    && (!requireFlag || registrationFlag)
                    && start != null && end != null && !now.isBefore(start) && !now.isAfter(end);
        }
    }



    // Tìm sinh viên từ tài khoản đang đăng nhập.
    StudentInfo studentByUsername(String username, boolean lock) {
        if (username == null || username.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa xác thực người dùng");
        return jdbc.query("""
            SELECT s.id, s.major_id FROM students s JOIN users u ON u.id = s.user_id
            WHERE u.username = ? AND u.status = 'active' AND u.role = 'student'
              AND u.is_public = 1 AND s.is_public = 1
            """ + (lock ? " FOR UPDATE" : ""),
            (rs, n) -> new StudentInfo(rs.getLong("id"), rs.getLong("major_id")), username)
            .stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Không tìm thấy hồ sơ sinh viên hợp lệ"));
    }

    // Lấy thông tin lớp học phần.
    SectionInfo section(Long id, boolean lock) {
        return jdbc.query("""
            SELECT id, course_id, semester_id, current_enrollment, max_capacity, status, is_public
            FROM sections WHERE id = ?
            """ + (lock ? " FOR UPDATE" : ""), (rs,n) -> new SectionInfo(
                rs.getLong("id"), rs.getLong("course_id"), rs.getLong("semester_id"),
                rs.getObject("current_enrollment", Integer.class), rs.getObject("max_capacity", Integer.class),
                rs.getString("status"), rs.getObject("is_public", Integer.class)), id)
            .stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp học phần"));
    }

    // Lấy trạng thái và thời gian đăng ký của học kỳ.
    SemesterInfo semester(Long id) {
        // Mặc định kiểm tra cả cờ trong db.sql lẫn khung giờ trong Semester.java.
        return jdbc.query("""
            SELECT is_active, is_public, is_registration_open, registration_start, registration_end
            FROM semesters WHERE id = ? FOR SHARE
            """, (rs,n) -> new SemesterInfo(rs.getObject("is_active", Integer.class), rs.getObject("is_public", Integer.class),
            rs.getBoolean("is_registration_open"), rs.getObject("registration_start", LocalDateTime.class),
            rs.getObject("registration_end", LocalDateTime.class), requireRegistrationFlag), id)
            .stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học kỳ"));
    }

    // Kiểm tra môn có thuộc chương trình đào tạo.
    boolean inMajor(Long majorId, Long courseId) {
        return exists("""
            SELECT COUNT(*) FROM major_courses mc
            JOIN courses c ON c.id = mc.course_id
            JOIN majors m ON m.id = mc.major_id
            WHERE mc.major_id = ? AND mc.course_id = ? AND c.is_public = 1 AND m.is_public = 1
            """, majorId, courseId);
    }

    // Tìm các môn tiên quyết sinh viên chưa hoàn thành.
    List<Map<String, Object>> missingPrerequisites(Long studentId, Long courseId) {
        return jdbc.queryForList("""
            SELECT DISTINCT c.id AS courseId, c.course_code AS courseCode, c.course_name AS courseName
            FROM prerequisites p JOIN courses c ON c.id = p.prerequisite_course_id
            WHERE p.course_id = ? AND NOT EXISTS (
                SELECT 1 FROM academic_results a WHERE a.student_id = ? AND a.course_id = c.id AND a.status = 'passed'
            ) ORDER BY c.course_code
            """, courseId, studentId);
    }

    // Kiểm tra trùng môn học trong cùng học kỳ.
    boolean duplicateCourse(Long studentId, SectionInfo section) {
        return exists("""
            SELECT COUNT(*) FROM enrollments e JOIN sections s ON s.id = e.section_id
            WHERE e.student_id = ? AND e.status IN ('registered','completed')
              AND s.course_id = ? AND s.semester_id = ?
            """, studentId, section.courseId(), section.semesterId());
    }

    // Kiểm tra trùng lịch học.
    boolean overlaps(Long studentId, SectionInfo section) {
        return exists("""
            SELECT COUNT(*) FROM schedules new_s
            JOIN schedules old_s ON old_s.day_of_week = new_s.day_of_week
              AND new_s.start_period <= old_s.end_period AND old_s.start_period <= new_s.end_period
              AND new_s.start_week <= old_s.end_week AND old_s.start_week <= new_s.end_week
            JOIN enrollments e ON e.section_id = old_s.section_id
            JOIN sections old_section ON old_section.id = e.section_id
            WHERE new_s.section_id = ? AND e.student_id = ? AND e.status = 'registered'
              AND old_section.semester_id = ? AND new_s.is_public = 1 AND old_s.is_public = 1
            """, section.id(), studentId, section.semesterId());
    }

    // Lấy danh sách đăng ký hiện tại.
    List<Map<String, Object>> currentEnrollments(Long studentId) {
        return jdbc.queryForList("""
            SELECT e.id, e.section_id AS sectionId, s.section_code AS sectionCode,
                   c.id AS courseId, c.course_code AS courseCode, c.course_name AS courseName,
                   c.credits, s.semester_id AS semesterId, e.registered_at AS registeredAt, e.status
            FROM enrollments e JOIN sections s ON s.id = e.section_id
            JOIN courses c ON c.id = s.course_id JOIN semesters sem ON sem.id = s.semester_id
            WHERE e.student_id = ? AND sem.is_active = 1 AND sem.is_public = 1 AND e.status IN ('registered','completed')
            ORDER BY e.registered_at DESC, e.id DESC
            """, studentId);
    }

    // Tăng sĩ số khi đăng ký thành công.
    void increment(Long sectionId) {
        int changed = jdbc.update("UPDATE sections SET current_enrollment = current_enrollment + 1 WHERE id = ? AND current_enrollment < max_capacity", sectionId);
        if (changed != 1) throw new ResponseStatusException(HttpStatus.CONFLICT, "Lớp đã hết chỗ hoặc sĩ số không hợp lệ");
    }

    // Giảm sĩ số khi hủy đăng ký.
    void decrement(Long sectionId) {
        int changed = jdbc.update("UPDATE sections SET current_enrollment = current_enrollment - 1 WHERE id = ? AND current_enrollment > 0", sectionId);
        if (changed != 1) throw new ResponseStatusException(HttpStatus.CONFLICT, "Sĩ số lớp không hợp lệ");
    }

    private boolean exists(String sql, Object... args) {
        Long count = jdbc.queryForObject(sql, Long.class, args);
        return count != null && count > 0;
    }

    }
}
