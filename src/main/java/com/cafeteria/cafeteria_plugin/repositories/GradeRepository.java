package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    void deleteByStudentId(Long id);

    List<Grade> findByStudentId(Long studentId);

    boolean existsByStudentIdAndClassSessionId(Long studentId, Long classSessionId);
}
