package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    private final TeacherMapper teacherMapper;

    @Autowired
    public StudentMapper(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    public StudentDTO toDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setUsername(student.getUsername());
        dto.setName(student.getName());
        dto.setPhoneNumber(student.getPhoneNumber());
        dto.setEmail(student.getEmail());
        dto.setClassId(student.getStudentClass() != null ? student.getStudentClass().getId() : null);


        if (student.getStudentClass() != null) {
            dto.setClassName(student.getStudentClass().getName());
            dto.setClassSpecialization(student.getStudentClass().getSpecialization());

            if (student.getStudentClass().getClassTeacher() != null) {
                TeacherDTO teacherDTO = teacherMapper.toDto(student.getStudentClass().getClassTeacher());
                dto.setClassTeacher(teacherDTO);
            }
        }

        return dto;
    }
}
