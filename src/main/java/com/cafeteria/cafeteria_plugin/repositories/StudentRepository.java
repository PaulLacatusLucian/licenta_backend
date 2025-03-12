package com.cafeteria.cafeteria_plugin.repositories;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByParentId(Long parentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Student s WHERE s.parent.id = :parentId")
    void deleteByParentId(@Param("parentId") Long parentId);

    List<Student> findByStudentClass(Class currentClass);

    List<Student> findByStudentClassIdIn(List<Long> classIds);
}
