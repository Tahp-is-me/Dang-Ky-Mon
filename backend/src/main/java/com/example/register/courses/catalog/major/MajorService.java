package com.example.register.courses.catalog.major;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MajorService {

    private final MajorRepository majorRepository;

    public MajorService(MajorRepository majorRepository) {
        this.majorRepository = majorRepository;
    }

    // Lấy tất cả ngành (bao gồm cả ngành đang ẩn để admin quản lý)
    public List<Major> getAllMajors() {
        return majorRepository.findAll();
    }

    // Lấy danh sách ngành đang công khai (isPublic = 1)
    public List<Major> getPublicMajors() {
        return majorRepository.findByIsPublic(1);
    }

    public Major getMajorById(Long id) {
        return majorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành học có ID: " + id));
    }

    public Major getMajorByCode(String majorCode) {
        return majorRepository.findByMajorCode(majorCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành học có mã: " + majorCode));
    }

    @Transactional
    public Major createMajor(Major major) {
        if (majorRepository.existsByMajorCode(major.getMajorCode())) {
            throw new RuntimeException("Lỗi: Mã ngành học đã tồn tại!");
        }
        if (major.getIsPublic() == null) {
            major.setIsPublic(1);
        }
        return majorRepository.save(major);
    }

    @Transactional
    public Major updateMajor(Long id, Major updatedMajor) {
        Major existingMajor = getMajorById(id);

        if (!existingMajor.getMajorCode().equals(updatedMajor.getMajorCode()) &&
                majorRepository.existsByMajorCode(updatedMajor.getMajorCode())) {
            throw new RuntimeException("Lỗi: Mã ngành học mới đã tồn tại!");
        }

        existingMajor.setMajorCode(updatedMajor.getMajorCode());
        existingMajor.setMajorName(updatedMajor.getMajorName());
        existingMajor.setDescription(updatedMajor.getDescription());

        if (updatedMajor.getIsPublic() != null) {
            existingMajor.setIsPublic(updatedMajor.getIsPublic());
        }

        return majorRepository.save(existingMajor);
    }

    // Xóa mềm: Đổi isPublic về 0
    @Transactional
    public void deleteMajor(Long id) {
        Major existingMajor = getMajorById(id);
        existingMajor.setIsPublic(0);
        majorRepository.save(existingMajor);
    }

    // Bật / Tắt lại trạng thái isPublic (1 <-> 0)
    @Transactional
    public Major toggleMajorStatus(Long id) {
        Major existingMajor = getMajorById(id);
        int newStatus = (existingMajor.getIsPublic() != null && existingMajor.getIsPublic() == 1) ? 0 : 1;
        existingMajor.setIsPublic(newStatus);
        return majorRepository.save(existingMajor);
    }
}