package com.example.register.courses.catalog.major;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {
    boolean existsByMajorCode(String majorCode);
    Optional<Major> findByMajorCode(String majorCode);
}