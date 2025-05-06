package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByName(String name);

    List<Teacher> findBySubject(String subject);


    @Query("SELECT t FROM Teacher t WHERE t.classAsTeacher.id = :classId AND t.type = 'EDUCATOR'")
    Teacher findEducatorByClassId(@Param("classId") Long classId);

    Teacher findByUsername(String username);
}
