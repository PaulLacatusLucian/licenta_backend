package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import org.springframework.stereotype.Component;

/**
 * Mapper-Komponente für die Konvertierung zwischen Teacher-Entitäten und TeacherDTO-Objekten.
 * @author Paul Lacatus
 * @version 1.0
 * @see Teacher
 * @see TeacherDTO
 * @see com.cafeteria.cafeteria_plugin.models.TeacherType
 * @see com.cafeteria.cafeteria_plugin.models.Class
 * @since 2025-03-13
 */
@Component
public class TeacherMapper {

    /**
     * Konvertiert eine Teacher-Entität zu einem umfassenden TeacherDTO.
     * @param teacher Teacher-Entität mit vollständigen Informationen, kann null sein
     * @return TeacherDTO mit aggregierten Informationen oder null bei null-Input
     */
    public static TeacherDTO toDto(Teacher teacher) {
        if (teacher == null) return null;

        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setUsername(teacher.getUsername());
        dto.setEmail(teacher.getEmail());
        dto.setName(teacher.getName());
        dto.setSubject(teacher.getSubject());

        // Organisatorische Status-Information für UI-Entscheidungen
        dto.setHasClassAssigned(teacher.getClassAsTeacher() != null);

        return dto;
    }
}