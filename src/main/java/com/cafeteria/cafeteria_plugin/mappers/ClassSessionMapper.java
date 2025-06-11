package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.ClassSessionDTO;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper-Komponente für die Konvertierung zwischen ClassSession-Entitäten und ClassSessionDTO-Objekten.
 * @author Paul Lacatus
 * @version 1.0
 * @see ClassSession
 * @see ClassSessionDTO
 * @see AbsenceMapper
 * @see GradeMapper
 * @see TeacherMapper
 * @since 2025-03-13
 */
@Component
public class ClassSessionMapper {

    /**
     * Mapper für Fehlzeiten-Konvertierung.
     * <p>
     * Wird für die Konvertierung der mit der Unterrichtsstunde verknüpften
     * Fehlzeiten verwendet. Integration erfolgt über Spring's Dependency Injection.
     */
    @Autowired
    private AbsenceMapper absenceMapper;

    /**
     * Mapper für Noten-Konvertierung.
     * <p>
     * Wird für die Konvertierung der mit der Unterrichtsstunde verknüpften
     * Noten verwendet. Integration erfolgt über Spring's Dependency Injection.
     */
    @Autowired
    private GradeMapper gradeMapper;

    /**
     * Mapper für Lehrer-Konvertierung.
     * <p>
     * Wird für die Konvertierung der Lehrerinformationen verwendet.
     * Integration erfolgt über Spring's Dependency Injection.
     */
    @Autowired
    private TeacherMapper teacherMapper;

    /**
     * Konvertiert eine ClassSession-Entität zu einem umfassenden ClassSessionDTO.
     * <p>
     * Diese Methode erstellt eine vollständige DTO-Darstellung einer Unterrichtsstunde
     * und aggregiert alle verwandten Bewertungsdaten. Sie ist optimiert für
     * Benutzeroberflächen, die komplette Unterrichtsstunden-Informationen benötigen.
     * @param session ClassSession-Entität mit vollständigen Beziehungen
     * @return ClassSessionDTO mit aggregierten Bewertungsdaten
     * @throws IllegalArgumentException wenn session null ist
     */
    public ClassSessionDTO toDto(ClassSession session) {
        ClassSessionDTO dto = new ClassSessionDTO();
        dto.setId(session.getId());
        dto.setSubject(session.getSubject());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setScheduleDay(session.getScheduleDay());
        dto.setClassName(session.getClassName());

        // Lehrer-Konvertierung mit vollständigen Informationen
        if (session.getTeacher() != null) {
            dto.setTeacher(teacherMapper.toDto(session.getTeacher()));
        }

        // Fehlzeiten-Konvertierung mit Stream-API für Performance
        if (session.getAbsences() != null) {
            dto.setAbsences(session.getAbsences()
                    .stream()
                    .map(absenceMapper::toDto)
                    .collect(Collectors.toList()));
        }

        // Noten-Konvertierung mit Stream-API für Performance
        if (session.getGrades() != null) {
            dto.setGrades(session.getGrades()
                    .stream()
                    .map(gradeMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * Konvertiert ein ClassSessionDTO zu einer ClassSession-Entität.
     * <p>
     * Diese Methode erstellt eine Basis-ClassSession-Entität aus DTO-Daten
     * für Persistierung oder weitere Verarbeitung. Sie fokussiert sich auf
     * die Kern-Unterrichtsstunden-Daten ohne komplexe Beziehungen.
     * @param dto ClassSessionDTO mit den zu konvertierenden Basis-Daten
     * @return ClassSession-Entität ohne komplexe Beziehungen
     * @throws IllegalArgumentException wenn dto null ist
     */
    public ClassSession toEntity(ClassSessionDTO dto) {
        ClassSession session = new ClassSession();
        session.setId(dto.getId());
        session.setSubject(dto.getSubject());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        session.setScheduleDay(dto.getScheduleDay());
        session.setClassName(dto.getClassName());
        return session;
    }
}