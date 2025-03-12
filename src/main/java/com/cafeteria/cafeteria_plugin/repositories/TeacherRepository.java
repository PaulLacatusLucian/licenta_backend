package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByName(String name);

    List<Teacher> findBySubject(String subject);
}
