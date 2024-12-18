package com.cafeteria.cafeteria_plugin.repositories;
import com.cafeteria.cafeteria_plugin.models.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByStudentId(Long studentId);
}
