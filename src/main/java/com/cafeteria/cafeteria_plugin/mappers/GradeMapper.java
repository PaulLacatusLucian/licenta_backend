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
        if (grade == null) {
            return null;
        }

        GradeDTO dto = new GradeDTO();

        // Copierea proprietăților de bază
        dto.setId(grade.getId());
        dto.setGrade(grade.getGrade());
        dto.setDescription(grade.getDescription());

        // Proprietăți referitoare la student
        if (grade.getStudent() != null) {
            dto.setStudentId(grade.getStudent().getId());
            dto.setStudentName(grade.getStudent().getName());
        }

        // Proprietăți referitoare la sesiunea de clasă
        if (grade.getClassSession() != null) {
            dto.setSessionDate(grade.getClassSession().getStartTime());

            // Setăm subiectul/materia
            if (grade.getClassSession().getSubject() != null) {
                dto.setSubject(grade.getClassSession().getSubject());
            }

            // Setăm numele profesorului
            if (grade.getClassSession().getTeacher() != null) {
                dto.setTeacherName(grade.getClassSession().getTeacher().getName());
            }
        }

        return dto;
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
