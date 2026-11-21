package com.example.register.courses.enrollment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.jdbc.core.JdbcTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
// Xử lý nhập điểm, xem kết quả và thống kê học tập.
public class AcademicResultService {
    private final AcademicResultRepository repository;
    private final Sql queries;
    private final BigDecimal passScore;

    public AcademicResultService(AcademicResultRepository repository, JdbcTemplate jdbc,
                                 @Value("${app.academic.pass-score:5.0}") BigDecimal passScore) {
        this.repository = repository;
        this.queries = new Sql(jdbc);
        this.passScore = passScore;
        if (passScore.compareTo(BigDecimal.ZERO) < 0 || passScore.compareTo(BigDecimal.TEN) > 0)
            throw new IllegalArgumentException("app.academic.pass-score phải từ 0 đến 10");
    }

    // Sinh viên xem bảng điểm cá nhân.
    @Transactional(readOnly = true)
    public Map<String, Object> getMyResults(String username) {
        return report(queries.studentByUsername(username, false).id());
    }

    @Transactional(readOnly = true)
    // Lấy danh sách sinh viên trong lớp để quản lý điểm.
    public List<Map<String, Object>> getSectionStudents(Long sectionId) {
        queries.section(sectionId, false);
        return queries.roster(sectionId);
    }

    // Kiểm tra điểm, tạo mới hoặc cập nhật kết quả học tập.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AcademicResult saveScore(Long studentId, Long sectionId, BigDecimal score) {
        if (score == null || score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.TEN) > 0 || score.scale() > 2)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Điểm phải từ 0 đến 10 và tối đa 2 chữ số thập phân");
        queries.requireStudent(studentId, true);
        var section = queries.section(sectionId, true);
        if (!queries.eligibleForGrade(studentId, sectionId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sinh viên không có đăng ký hợp lệ trong lớp");
        AcademicResult result = repository.findByStudentIdAndSectionId(studentId, sectionId).orElseGet(AcademicResult::new);
        result.setStudentId(studentId);
        result.setSectionId(sectionId);
        result.setCourseId(section.courseId());
        result.setTotalScore(score);
        result.setStatus(score.compareTo(passScore) >= 0 ? AcademicResult.Status.passed : AcademicResult.Status.failed);
        result.setUpdatedAt(LocalDateTime.now());
        return repository.saveAndFlush(result);
    }

    @Transactional(readOnly = true)
    // Xem báo cáo kết quả học tập của một sinh viên.
    public Map<String, Object> getStudentReport(Long studentId) {
        queries.requireStudent(studentId, false);
        return report(studentId);
    }

    // Tổng hợp bảng điểm, tín chỉ đạt và các môn nợ.
    private Map<String, Object> report(Long studentId) {
        var debts = queries.debts(studentId);
        return Map.of("studentId", studentId, "results", queries.results(studentId),
                      "passedCredits", queries.passedCredits(studentId), "debtCourses", debts, "debtCount", debts.size());
    }


    // Các truy vấn SQL phục vụ bảng điểm và báo cáo.
    static final class Sql {
        private final JdbcTemplate jdbc;

        Sql(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        record StudentInfo(Long id, Long majorId) {}
        record SectionInfo(Long id, Long courseId, Long semesterId, Integer current, Integer capacity, String status, Integer isPublic) {}

    // Tìm sinh viên theo tài khoản đăng nhập.
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

    // Kiểm tra sinh viên có tồn tại.
    void requireStudent(Long id, boolean lock) {
        if (jdbc.queryForList("SELECT id FROM students WHERE id = ?" + (lock ? " FOR UPDATE" : ""), id).isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên");
    }

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

    // Kiểm tra đăng ký hợp lệ trước khi nhập điểm.
    boolean eligibleForGrade(Long studentId, Long sectionId) {
        return exists("SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND section_id = ? AND status IN ('registered','completed')", studentId, sectionId);
    }

    // Danh sách sinh viên của lớp.
    List<Map<String, Object>> roster(Long sectionId) {
        return jdbc.queryForList("""
            SELECT DISTINCT s.id AS studentId, s.student_code AS studentCode, u.full_name AS fullName,
                a.id AS resultId, a.total_score AS totalScore, a.status AS resultStatus
            FROM enrollments e JOIN students s ON s.id = e.student_id JOIN users u ON u.id = s.user_id
            LEFT JOIN academic_results a ON a.student_id = s.id AND a.section_id = e.section_id
            WHERE e.section_id = ? AND e.status IN ('registered','completed') ORDER BY s.student_code
            """, sectionId);
    }

    // Danh sách kết quả học tập.
    List<Map<String, Object>> results(Long studentId) {
        return jdbc.queryForList("""
            SELECT a.id, a.course_id AS courseId, c.course_code AS courseCode, c.course_name AS courseName,
                   c.credits, a.section_id AS sectionId, a.total_score AS totalScore, a.status, a.updated_at AS updatedAt
            FROM academic_results a JOIN courses c ON c.id = a.course_id
            WHERE a.student_id = ? ORDER BY a.updated_at DESC, a.id DESC
            """, studentId);
    }

    // Danh sách môn chưa qua.
    List<Map<String, Object>> debts(Long studentId) {
        return jdbc.queryForList("""
            SELECT DISTINCT c.id AS courseId, c.course_code AS courseCode, c.course_name AS courseName, c.credits
            FROM academic_results a JOIN courses c ON c.id = a.course_id
            WHERE a.student_id = ? AND a.status IN ('failed','debt')
              AND NOT EXISTS (SELECT 1 FROM academic_results p WHERE p.student_id = a.student_id AND p.course_id = a.course_id AND p.status = 'passed')
            ORDER BY c.course_code
            """, studentId);
    }

    // Tổng tín chỉ đã đạt.
    int passedCredits(Long studentId) {
        Integer total = jdbc.queryForObject("""
            SELECT COALESCE(SUM(c.credits), 0) FROM courses c WHERE EXISTS (
                SELECT 1 FROM academic_results a WHERE a.course_id = c.id AND a.student_id = ? AND a.status = 'passed')
            """, Integer.class, studentId);
        return total == null ? 0 : total;
    }

    private boolean exists(String sql, Object... args) {
        Long count = jdbc.queryForObject(sql, Long.class, args);
        return count != null && count > 0;
    }

    }
}
