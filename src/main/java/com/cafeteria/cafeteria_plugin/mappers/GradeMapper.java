package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.GradeDTO;
import com.cafeteria.cafeteria_plugin.models.Grade;
import org.springframework.stereotype.Component;

@Component
public class GradeMapper {

    public GradeDTO toDto(Grade grade) {
        GradeDTO dto = new GradeDTO(
                grade.getGrade(),
                grade.getTeacher().getName(),
                grade.getClassSession().getSubject(),
                grade.getClassSession().getStartTime()
        );
        return dto;
    }
}
