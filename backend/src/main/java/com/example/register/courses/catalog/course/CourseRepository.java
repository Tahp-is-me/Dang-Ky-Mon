package com.example.register.courses.catalog.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByCourseCode(String courseCode);

    List<Course> findByIsPublic(Integer isPublic);

    @Query("SELECT c FROM Course c WHERE " +
            "(:keyword IS NULL OR LOWER(c.courseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:credits IS NULL OR c.credits = :credits) AND " +
            "(:isPublic IS NULL OR c.isPublic = :isPublic)")
    Page<Course> searchCourses(@Param("keyword") String keyword,
                               @Param("credits") Integer credits,
                               @Param("isPublic") Integer isPublic,
                               Pageable pageable);
}