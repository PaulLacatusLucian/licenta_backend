package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

    // Găsirea sesiunilor după profesor
    List<ClassSession> findByTeacherId(Long teacherId);

    // Găsirea sesiunilor pentru o anumită materie
    List<ClassSession> findBySubject(String subject);

    // Găsirea sesiunilor dintr-un interval de timp
    List<ClassSession> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<ClassSession> findByTeacher_Id(Long teacherId);

}
