package com.example.register.courses.catalog.majorcourse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MajorCourseRepository extends JpaRepository<MajorCourse, Long> {

    boolean existsByMajorIdAndCourseId(Long majorId, Long courseId);

    List<MajorCourse> findByMajorId(Long majorId);

    List<MajorCourse> findByMajorIdAndCourseType(Long majorId, String courseType);
}