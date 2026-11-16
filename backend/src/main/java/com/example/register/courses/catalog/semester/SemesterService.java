package com.example.register.courses.catalog.semester;

import com.example.register.courses.catalog.semester.dto.CreateSemesterRequest;
import com.example.register.courses.catalog.semester.dto.UpdateRegistrationWindowRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SemesterService {

    private final SemesterRepository semesterRepository;

    public SemesterService(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    // Hàm kiểm tra tính hợp lệ của các mốc thời gian
    private void validateSemesterDates(LocalDate startDate, LocalDate endDate,
                                       LocalDateTime regStart, LocalDateTime regEnd) {
        if (!endDate.isAfter(startDate)) {
            throw new RuntimeException("Lỗi: Ngày kết thúc học kỳ phải sau ngày bắt đầu học kỳ!");
        }

        if ((regStart != null && regEnd == null) || (regStart == null && regEnd != null)) {
            throw new RuntimeException("Lỗi: Phải nhập đầy đủ cả thời gian mở và đóng đăng ký tín chỉ!");
        }

        if (regStart != null && regEnd != null) {
            if (!regEnd.isAfter(regStart)) {
                throw new RuntimeException("Lỗi: Thời gian đóng đăng ký phải sau thời gian mở đăng ký!");
            }
            if (regEnd.toLocalDate().isAfter(endDate)) {
                throw new RuntimeException("Lỗi: Thời gian đóng đăng ký không được vượt quá ngày kết thúc học kỳ!");
            }
        }
    }

    // 1. Tạo mới học kỳ
    @Transactional
    public Semester createSemester(CreateSemesterRequest request) {
        if (semesterRepository.existsBySemesterCode(request.getSemesterCode())) {
            throw new RuntimeException("Lỗi: Mã học kỳ đã tồn tại!");
        }

        validateSemesterDates(
                request.getStartDate(),
                request.getEndDate(),
                request.getRegistrationStart(),
                request.getRegistrationEnd()
        );

        int activeFlag = (request.getIsActive() != null && request.getIsActive() == 1) ? 1 : 0;
        if (activeFlag == 1) {
            semesterRepository.deactivateAllSemesters();
        }

        Semester semester = Semester.builder()
                .semesterCode(request.getSemesterCode())
                .semesterName(request.getSemesterName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .registrationStart(request.getRegistrationStart())
                .registrationEnd(request.getRegistrationEnd())
                .isActive(activeFlag)
                .build();

        return semesterRepository.save(semester);
    }

    // 2. Lấy danh sách tất cả học kỳ (sắp xếp mới nhất lên đầu)
    public List<Semester> getAllSemesters() {
        return semesterRepository.findAllByOrderByStartDateDesc();
    }

    // 3. Lấy học kỳ hiện hành (isActive = 1)
    public Semester getActiveSemester() {
        return semesterRepository.findFirstByIsActive(1)
                .orElseThrow(() -> new RuntimeException("Hiện chưa có học kỳ nào được kích hoạt!"));
    }

    // 4. Lấy chi tiết 1 học kỳ
    public Semester getSemesterById(Long id) {
        return semesterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ có ID: " + id));
    }

    // 5. Cập nhật toàn bộ thông tin học kỳ (phòng trường hợp nhập sai ngày/tên)
    @Transactional
    public Semester updateSemester(Long id, CreateSemesterRequest request) {
        Semester semester = getSemesterById(id);

        if (!semester.getSemesterCode().equals(request.getSemesterCode())
                && semesterRepository.existsBySemesterCode(request.getSemesterCode())) {
            throw new RuntimeException("Lỗi: Mã học kỳ mới đã tồn tại!");
        }

        validateSemesterDates(
                request.getStartDate(),
                request.getEndDate(),
                request.getRegistrationStart(),
                request.getRegistrationEnd()
        );

        if (request.getIsActive() != null && request.getIsActive() == 1 && semester.getIsActive() != 1) {
            semesterRepository.deactivateAllSemesters();
            semester.setIsActive(1);
        } else if (request.getIsActive() != null) {
            semester.setIsActive(request.getIsActive() == 1 ? 1 : 0);
        }

        semester.setSemesterCode(request.getSemesterCode());
        semester.setSemesterName(request.getSemesterName());
        semester.setStartDate(request.getStartDate());
        semester.setEndDate(request.getEndDate());
        semester.setRegistrationStart(request.getRegistrationStart());
        semester.setRegistrationEnd(request.getRegistrationEnd());

        return semesterRepository.save(semester);
    }

    // 6. Cập nhật / gia hạn nhanh khung giờ đăng ký tín chỉ
    @Transactional
    public Semester updateRegistrationWindow(Long id, UpdateRegistrationWindowRequest request) {
        Semester semester = getSemesterById(id);

        validateSemesterDates(
                semester.getStartDate(),
                semester.getEndDate(),
                request.getRegistrationStart(),
                request.getRegistrationEnd()
        );

        semester.setRegistrationStart(request.getRegistrationStart());
        semester.setRegistrationEnd(request.getRegistrationEnd());

        return semesterRepository.save(semester);
    }

    // 7. Kích hoạt 1 học kỳ làm học kỳ hiện hành (tắt các học kỳ khác)
    @Transactional
    public Semester activateSemester(Long id) {
        Semester semester = getSemesterById(id);
        semesterRepository.deactivateAllSemesters();
        semester.setIsActive(1);
        return semesterRepository.save(semester);
    }

    // 8. Xóa học kỳ
    @Transactional
    public void deleteSemester(Long id) {
        Semester semester = getSemesterById(id);
        if (semester.getIsActive() == 1) {
            throw new RuntimeException("Lỗi: Không thể xóa học kỳ đang được kích hoạt (isActive = 1)!");
        }
        semesterRepository.delete(semester);
    }
}