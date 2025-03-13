package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.ClassSessionDTO;
import com.cafeteria.cafeteria_plugin.dtos.GradeDTO;
import com.cafeteria.cafeteria_plugin.dtos.AbsenceDTO;
import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ClassSessionMapper {

    @Autowired
    private AbsenceMapper absenceMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    public ClassSessionDTO toDto(ClassSession session) {
        ClassSessionDTO dto = new ClassSessionDTO();
        dto.setId(session.getId());
        dto.setSubject(session.getSubject());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());

        if (session.getTeacher() != null) {
            dto.setTeacher(teacherMapper.toDto(session.getTeacher()));
        }

        if (session.getAbsences() != null) {
            dto.setAbsences(session.getAbsences()
                    .stream()
                    .map(absenceMapper::toDto)
                    .collect(Collectors.toList()));
        }

        if (session.getGrades() != null) {
            dto.setGrades(session.getGrades()
                    .stream()
                    .map(gradeMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public ClassSession toEntity(ClassSessionDTO dto) {
        ClassSession session = new ClassSession();
        session.setId(dto.getId());
        session.setSubject(dto.getSubject());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        // teacher, absences și grades se setează separat în controller
        return session;
    }
}
