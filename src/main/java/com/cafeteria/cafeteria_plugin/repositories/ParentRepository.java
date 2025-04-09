package com.cafeteria.cafeteria_plugin.repositories;
import com.cafeteria.cafeteria_plugin.models.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByMotherEmail(String motherEmail);
    Optional<Parent> findByFatherEmail(String fatherEmail);

    @Query("SELECT DISTINCT p FROM Parent p JOIN p.students s WHERE s.studentClass.id = :classId")
    List<Parent> findDistinctByStudents_StudentClass_Id(@Param("classId") Long classId);
}
