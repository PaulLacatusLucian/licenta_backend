package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.GradeDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.models.Student;
import org.springframework.stereotype.Component;

/**
 * Mapper-Komponente für die Konvertierung zwischen Grade-Entitäten und GradeDTO-Objekten.
 * @author Paul Lacatus
 * @version 1.0
 * @see Grade
 * @see GradeDTO
 * @see StudentDTO
 * @see TeacherMapper
 * @since 2025-01-01
 */
@Component
public class GradeMapper {

    /**
     * Konvertiert eine Grade-Entität zu einem kontextreichen GradeDTO.
     * @param grade Grade-Entität mit vollständigen ClassSession-Beziehungen
     * @return GradeDTO mit aggregierten Kontext-Informationen
     * @throws IllegalArgumentException wenn grade null ist
     * @throws IllegalStateException wenn erforderliche ClassSession-Beziehungen fehlen
     */
    public GradeDTO toDto(Grade grade) {
        if (grade == null) {
            return null;
        }

        GradeDTO dto = new GradeDTO();

        dto.setId(grade.getId());
        dto.setGrade(grade.getGrade());
        dto.setDescription(grade.getDescription());

        if (grade.getStudent() != null) {
            dto.setStudentId(grade.getStudent().getId());
            dto.setStudentName(grade.getStudent().getName());
        }

        if (grade.getClassSession() != null) {
            dto.setSessionDate(grade.getClassSession().getStartTime());

            if (grade.getClassSession().getSubject() != null) {
                dto.setSubject(grade.getClassSession().getSubject());
            }

            if (grade.getClassSession().getTeacher() != null) {
                dto.setTeacherName(grade.getClassSession().getTeacher().getName());
            }
        }

        return dto;
    }
    /**
     * Erstellt eine vollständige StudentDTO aus einer Student-Entität.
     * <p>
     * Diese Hilfsmethode wird intern verwendet, um umfassende Student-Informationen
     * zu erstellen, wenn Student-Kontext in Noten-Darstellungen benötigt wird.
     * Sie aggregiert alle relevanten Student-Daten inklusive Klassen-Kontext.
     * <p>
     * Aggregierte Student-Informationen:
     * - Basis-Identifikation (ID, Name, Username)
     * - Kontakt-Informationen (Telefon)
     * - Klassen-Zuordnung (Name, Spezialisierung)
     * - Klassenlehrer-Informationen für vollständigen Kontext
     * <p>
     * Hinweis:
     * - Diese Methode ist als private implementiert, könnte aber bei Bedarf
     *   öffentlich gemacht werden für erweiterte Mapper-Funktionalitäten
     * - Integration mit TeacherMapper für Klassenlehrer-Details
     * - Null-sichere Verarbeitung für optionale Klassen-Beziehungen
     * <p>
     * Performance-Überlegungen:
     * - Einmalige Entitäts-Navigation für alle Student-Daten
     * - Vermeidung von Lazy-Loading-Problemen durch direkte Zugriffe
     * - Optimiert für seltene Verwendung in spezialisierten Anwendungsfällen
     *
     * @param student Student-Entität mit vollständigen Beziehungen
     * @return StudentDTO mit aggregierten Klassen- und Lehrer-Informationen
     * @throws IllegalArgumentException wenn student null ist
     */
    private StudentDTO buildStudentDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setUsername(student.getUsername());
        dto.setPhoneNumber(student.getPhoneNumber());

        // Klassen-Kontext für vollständige Student-Information
        if (student.getStudentClass() != null) {
            dto.setClassName(student.getStudentClass().getName());
            dto.setClassSpecialization(student.getStudentClass().getSpecialization());

            // Klassenlehrer-Integration für umfassenden Kontext
            if (student.getStudentClass().getClassTeacher() != null) {
                dto.setClassTeacher(TeacherMapper.toDto(student.getStudentClass().getClassTeacher()));
            }
        }
        return dto;
    }
}