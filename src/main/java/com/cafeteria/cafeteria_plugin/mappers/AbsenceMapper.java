package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.AbsenceDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper-Komponente für die Konvertierung zwischen Absence-Entitäten und AbsenceDTO-Objekten.
 * @author Paul Lacatus
 * @version 1.0
 * @see Absence
 * @see AbsenceDTO
 * @see StudentDTO
 * @see TeacherDTO
 * @since 2025-03-13
 */
@Component
public class AbsenceMapper {

    /**
     * Konvertiert eine AbsenceDTO zu einer Absence-Entität mit vollständigen Abhängigkeiten.
     * @param dto AbsenceDTO mit den zu konvertierenden Daten
     * @param student Student-Entität für die Fehlzeit
     * @param classSession ClassSession-Entität der Unterrichtsstunde
     * @param teacher Teacher-Entität des erfassenden Lehrers
     * @return Vollständig konfigurierte Absence-Entität
     * @throws IllegalArgumentException wenn erforderliche Parameter null sind
     */
    public Absence toEntity(AbsenceDTO dto, Student student, ClassSession classSession, Teacher teacher) {
        Absence entity = new Absence();
        entity.setId(dto.getId());
        entity.setStudent(student);
        entity.setClassSession(classSession);

        return entity;
    }

    /**
     * Vereinfachte Konvertierung von AbsenceDTO zu Absence-Entität.
     * <p>
     * Diese Methode stellt eine kompatible Überladung für bestehenden Code dar,
     * der nicht explizit den erfassenden Lehrer angibt. Sie verwendet den
     * Lehrer der ClassSession als Standard-Teacher für die Fehlzeit.
     * <p>
     * Verwendung:
     * - Legacy-Code-Kompatibilität
     * - Vereinfachte API-Endpunkte
     * - Standard-Fehlzeiten-Erfassung
     *
     * @param dto AbsenceDTO mit den zu konvertierenden Daten
     * @param student Student-Entität für die Fehlzeit
     * @param classSession ClassSession-Entität der Unterrichtsstunde
     * @return Absence-Entität mit ClassSession-Teacher als Standard
     */
    public Absence toEntity(AbsenceDTO dto, Student student, ClassSession classSession) {
        return toEntity(dto, student, classSession, classSession.getTeacher());
    }

    /**
     * Konvertiert eine Absence-Entität zu einem AbsenceDTO mit vollständigen Informationen.
     * <p>
     * Diese Methode erstellt eine umfassende DTO-Darstellung einer Fehlzeit-Entität
     * und aggregiert alle relevanten Informationen aus verknüpften Entitäten.
     * Sie ist optimiert für API-Responses und Frontend-Darstellungen.
     * <p>
     * @param absence Absence-Entität mit vollständigen Beziehungen
     * @return AbsenceDTO mit aggregierten Informationen für API-Response
     * @throws IllegalArgumentException wenn absence null ist
     * @throws IllegalStateException wenn erforderliche Beziehungen fehlen
     */
    public AbsenceDTO toDto(Absence absence) {
        AbsenceDTO dto = new AbsenceDTO();
        dto.setId(absence.getId());
        dto.setClassSessionId(absence.getClassSession().getId());

        // Verwendung der ClassSession-Startzeit als Fehlzeit-Datum (nicht aktuelle Zeit)
        LocalDateTime sessionDate = absence.getClassSession().getStartTime();
        dto.setSessionDate(sessionDate);

        // Erstellung der Teacher-DTO für den erfassenden Lehrer (Fachlehrer)
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(absence.getClassSession().getTeacher().getId());
        teacherDTO.setName(absence.getClassSession().getTeacher().getName());
        teacherDTO.setSubject(absence.getClassSession().getSubject());
        teacherDTO.setEmail(absence.getClassSession().getTeacher().getEmail());

        dto.setTeacherWhoMarkedAbsence(teacherDTO);

        // Vollständige Student-Informationen mit Klassen-Kontext
        Student student = absence.getStudent();
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setName(student.getName());
        studentDTO.setUsername(student.getUsername());
        studentDTO.setPhoneNumber(student.getPhoneNumber());

        // Klassen-Informationen für Student-Kontext
        if (student.getStudentClass() != null) {
            studentDTO.setClassName(student.getStudentClass().getName());
            studentDTO.setClassSpecialization(student.getStudentClass().getSpecialization());

            // Klassenlehrer-Informationen (unterscheidet sich vom erfassenden Fachlehrer)
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
        dto.setJustified(absence.getJustified());
        return dto;
    }
}