package com.cafeteria.cafeteria_plugin.repositories;
import com.cafeteria.cafeteria_plugin.models.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    @Query("SELECT COUNT(a) FROM Absence a WHERE a.student.id = :studentId")
    int countByStudentId(@Param("studentId") Long studentId);

    List<Absence> findByStudentId(Long studentId);

    boolean existsByStudentIdAndClassSessionId(Long id, Long id1);

    @Query("SELECT a FROM Absence a WHERE a.student.id IN :studentIds AND (a.justified = false OR a.justified IS NULL)")
    List<Absence> findUnjustifiedAbsencesByStudentIds(@Param("studentIds") List<Long> studentIds);}
