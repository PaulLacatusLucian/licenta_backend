package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.GradeDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.models.Student;
import org.springframework.stereotype.Component;

@Component
public class GradeMapper {

    public GradeDTO toDto(Grade grade) {
        return new GradeDTO(
                grade.getGrade(),
                grade.getDescription(),
                grade.getClassSession().getTeacher().getName(),
                grade.getClassSession().getSubject(),
                grade.getClassSession().getStartTime()
        );
    }

    private StudentDTO buildStudentDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setUsername(student.getUsername());
        dto.setPhoneNumber(student.getPhoneNumber());

        if (student.getStudentClass() != null) {
            dto.setClassName(student.getStudentClass().getName());
            dto.setClassSpecialization(student.getStudentClass().getSpecialization());
            if (student.getStudentClass().getClassTeacher() != null) {
                dto.setClassTeacher(TeacherMapper.toDto(student.getStudentClass().getClassTeacher()));
            }
        }
        return dto;
    }

}
