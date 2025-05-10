package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.AbsenceDTO;
import com.cafeteria.cafeteria_plugin.models.Absence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbsenceMapper {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    public AbsenceDTO toDto(Absence absence) {
        if (absence == null) {
            return null;
        }

        AbsenceDTO dto = new AbsenceDTO();

        dto.setId(absence.getId());
        dto.setJustified(absence.getJustified());

        if (absence.getStudent() != null) {
            dto.setStudent(studentMapper.toDTO(absence.getStudent()));
        }

        if (absence.getTeacher() != null) {
            dto.setTeacherWhoMarkedAbsence(teacherMapper.toDto(absence.getTeacher()));
        }

        if (absence.getClassSession() != null) {
            // Extragem doar informațiile necesare fără a apela classSessionMapper
            dto.setClassSessionId(absence.getClassSession().getId());
            dto.setSubject(absence.getClassSession().getSubject());
            dto.setClassName(absence.getClassSession().getClassName());
            dto.setDate(absence.getClassSession().getStartTime());
        }

        return dto;
    }

    public Absence toEntity(AbsenceDTO dto) {
        if (dto == null) {
            return null;
        }

        Absence absence = new Absence();
        absence.setId(dto.getId());
        absence.setJustified(dto.getJustified());

        return absence;
    }
}