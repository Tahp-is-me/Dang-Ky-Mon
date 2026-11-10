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

    public List<Major> getAllMajors() {
        return majorRepository.findAll();
    }

    public Major getMajorById(Long id) {
        return majorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành học có ID: " + id));
    }

    @Transactional
    public Major createMajor(Major major) {
        if (majorRepository.existsByMajorCode(major.getMajorCode())) {
            throw new RuntimeException("Lỗi: Mã ngành học đã tồn tại!");
        }
        return majorRepository.save(major);
    }

    @Transactional
    public Major updateMajor(Long id, Major updatedMajor) {
        Major existingMajor = getMajorById(id);

        // Kiểm tra nếu đổi mã ngành mà mã mới đã tồn tại ở bản ghi khác
        if (!existingMajor.getMajorCode().equals(updatedMajor.getMajorCode()) &&
                majorRepository.existsByMajorCode(updatedMajor.getMajorCode())) {
            throw new RuntimeException("Lỗi: Mã ngành học mới đã tồn tại!");
        }

        existingMajor.setMajorCode(updatedMajor.getMajorCode());
        existingMajor.setMajorName(updatedMajor.getMajorName());
        existingMajor.setDescription(updatedMajor.getDescription());
        return majorRepository.save(existingMajor);
    }

    @Transactional
    public void deleteMajor(Long id) {
        Major existingMajor = getMajorById(id);
        majorRepository.delete(existingMajor);
    }
}