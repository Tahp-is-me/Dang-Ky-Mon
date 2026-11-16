package com.example.register.courses.catalog.semester;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    boolean existsBySemesterCode(String semesterCode);

    Optional<Semester> findFirstByIsActive(Integer isActive);

    List<Semester> findAllByOrderByStartDateDesc();

    @Modifying
    @Query("UPDATE Semester s SET s.isActive = 0 WHERE s.isActive = 1")
    void deactivateAllSemesters();
}