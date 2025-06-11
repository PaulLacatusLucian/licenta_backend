package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper-Komponente für die Konvertierung zwischen Student-Entitäten und StudentDTO-Objekten.
 * @author Paul Lacatus
 * @version 1.0
 * @see Student
 * @see StudentDTO
 * @see TeacherMapper
 * @see com.cafeteria.cafeteria_plugin.models.Class
 * @since 2025-03-13
 */
@Component
public class StudentMapper {

    /**
     * Mapper für Lehrer-Konvertierung.
     * <p>
     * Wird für die Konvertierung der Klassenlehrer-Informationen verwendet.
     * Integration erfolgt über Spring's Dependency Injection für
     * konsistente Mapper-Verwendung im gesamten System.
     */
    private final TeacherMapper teacherMapper;

    /**
     * Konstruktor für Dependency Injection des TeacherMapper.
     * <p>
     * Spring's Constructor Injection sorgt für sichere und testbare
     * Abhängigkeitsverwaltung. Der TeacherMapper wird zur Laufzeit
     * automatisch injiziert und für Klassenlehrer-Konvertierung verwendet.
     *
     * @param teacherMapper Mapper für Lehrer-Konvertierungen
     */
    @Autowired
    public StudentMapper(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    /**
     * Konvertiert eine Student-Entität zu einem umfassenden StudentDTO.
     * @param student Student-Entität mit vollständigen Klassen- und Lehrer-Beziehungen
     * @return StudentDTO mit aggregierten Informationen für sichere API-Übertragung
     * @throws IllegalArgumentException wenn student null ist
     */
    public StudentDTO toDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setUsername(student.getUsername());
        dto.setName(student.getName());
        dto.setPhoneNumber(student.getPhoneNumber());
        dto.setEmail(student.getEmail());
        dto.setClassId(student.getStudentClass() != null ? student.getStudentClass().getId() : null);
        dto.setProfileImage(student.getProfileImage());

        // Klassen-Kontext-Aggregation für vollständige Schüler-Information
        if (student.getStudentClass() != null) {
            dto.setClassName(student.getStudentClass().getName());
            dto.setClassSpecialization(student.getStudentClass().getSpecialization());

            // Klassenlehrer-Integration für Eltern-Kommunikation
            if (student.getStudentClass().getClassTeacher() != null) {
                TeacherDTO teacherDTO = teacherMapper.toDto(student.getStudentClass().getClassTeacher());
                dto.setClassTeacher(teacherDTO);
            }
        }

        return dto;
    }
}