package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import org.springframework.stereotype.Component;

@Component
public class TeacherMapper {
    public static TeacherDTO toDto(Teacher teacher) {
        if (teacher == null) return null;

        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setUsername(teacher.getUsername());
        dto.setEmail(teacher.getEmail());
        dto.setName(teacher.getName());
        dto.setSubject(teacher.getSubject());
        return dto;
    }
}
