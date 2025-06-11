package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.ScheduleDTO;
import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.Schedule;
import org.springframework.stereotype.Component;

/**
 * Mapper-Komponente für die Konvertierung zwischen Schedule-Entitäten und ScheduleDTO-Objekten.
 * @author Paul Lacatus
 * @version 1.0
 * @see Schedule
 * @see ScheduleDTO
 * @see TeacherDTO
 * @see com.cafeteria.cafeteria_plugin.models.Class
 * @since 2025-03-24
 */
@Component
public class ScheduleMapper {

    /**
     * Konvertiert eine Schedule-Entität zu einem umfassenden ScheduleDTO.
     * @param schedule Schedule-Entität mit vollständigen Lehrer- und Klassen-Beziehungen
     * @return ScheduleDTO mit aggregierten Informationen für Frontend-Darstellung
     * @throws IllegalArgumentException wenn schedule null ist
     * @throws IllegalStateException wenn erforderliche Teacher-Beziehung fehlt
     */
    public ScheduleDTO toDto(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setScheduleDay(schedule.getScheduleDay());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setSubjects(schedule.getSubjects());

        // Vollständige Lehrer-Informationen für Kontext und Kommunikation
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(schedule.getTeacher().getId());
        teacherDTO.setName(schedule.getTeacher().getName());
        teacherDTO.setUsername(schedule.getTeacher().getUsername());
        teacherDTO.setEmail(schedule.getTeacher().getEmail());
        teacherDTO.setSubject(schedule.getTeacher().getSubject());
        dto.setTeacher(teacherDTO);

        // Klassen-Kontext für organisatorische Zuordnung
        if (schedule.getStudentClass() != null) {
            dto.setClassName(schedule.getStudentClass().getName());
        }

        return dto;
    }
}