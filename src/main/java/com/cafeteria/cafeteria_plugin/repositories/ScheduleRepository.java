package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s WHERE s.studentClass.id = :classId")
    List<Schedule> findAllByClassIdWithTeacher(Long classId);

    List<Schedule> findByTeacherId(Long teacherId);

    @Query("SELECT s FROM Schedule s JOIN s.studentClass c WHERE c.name = :className")
    List<Schedule> findAllByClassNameWithTeacher(@Param("className") String className);
}
