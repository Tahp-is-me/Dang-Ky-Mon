package com.example.register.courses.catalog.lecturer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    boolean existsByLecturerCode(String lecturerCode);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Lecturer> findByIsPublic(Integer isPublic);

    @Query("SELECT DISTINCT l.department FROM Lecturer l WHERE l.department IS NOT NULL AND l.department <> ''")
    List<String> findDistinctDepartments();

    @Query("SELECT l FROM Lecturer l WHERE " +
            "(:keyword IS NULL OR LOWER(l.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(l.lecturerCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:department IS NULL OR l.department = :department) AND " +
            "(:isPublic IS NULL OR l.isPublic = :isPublic)")
    Page<Lecturer> searchLecturers(@Param("keyword") String keyword,
                                   @Param("department") String department,
                                   @Param("isPublic") Integer isPublic,
                                   Pageable pageable);
}