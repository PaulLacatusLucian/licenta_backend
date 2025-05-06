package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.AbsenceDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Student;
import org.springframework.stereotype.Component;

@Component
public class AbsenceMapper {

    public Absence toEntity(AbsenceDTO dto, Student student, ClassSession classSession) {
        Absence entity = new Absence();
        entity.setId(dto.getId());
        entity.setStudent(student);
        entity.setClassSession(classSession);
        // NU mai setăm explicit teacher/subject, se obțin automat din classSession
        return entity;
    }

    public AbsenceDTO toDto(Absence absence) {
        AbsenceDTO dto = new AbsenceDTO();
        dto.setId(absence.getId());
        dto.setClassSessionId(absence.getClassSession().getId());

        Student student = absence.getStudent();
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setName(student.getName());
        studentDTO.setUsername(student.getUsername());
        studentDTO.setPhoneNumber(student.getPhoneNumber());

        if (student.getStudentClass() != null) {
            studentDTO.setClassName(student.getStudentClass().getName());
            studentDTO.setClassSpecialization(student.getStudentClass().getSpecialization());

            if (student.getStudentClass().getClassTeacher() != null) {
                TeacherDTO classTeacherDTO = new TeacherDTO();
                classTeacherDTO.setId(student.getStudentClass().getClassTeacher().getId());
                classTeacherDTO.setName(student.getStudentClass().getClassTeacher().getName());
                classTeacherDTO.setSubject(student.getStudentClass().getClassTeacher().getSubject());
                classTeacherDTO.setUsername(student.getStudentClass().getClassTeacher().getUsername());
                classTeacherDTO.setEmail(student.getStudentClass().getClassTeacher().getEmail());

                studentDTO.setClassTeacher(classTeacherDTO);
            }
        }

        dto.setStudent(studentDTO);
        return dto;
    }



}
