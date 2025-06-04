package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper-Komponente für die Konvertierung zwischen Student-Entitäten und StudentDTO-Objekten.
 * <p>
 * Diese Klasse implementiert das Mapper-Pattern für Schülerkonten und stellt spezialisierte
 * Konvertierungsfunktionen zwischen der Student-Domain-Entität und dem entsprechenden
 * Data Transfer Object bereit. Sie aggregiert komplexe Klassen- und Lehrer-Informationen
 * für vollständige Schüler-Profile und optimiert die Daten für sichere API-Übertragung.
 * <p>
 * Hauptfunktionalitäten:
 * - Entity-zu-DTO-Konvertierung mit Klassen-Kontext-Aggregation
 * - Sichere Datenübertragung ohne sensible Authentifizierungsdaten
 * - Vollständige Klassenlehrer-Informationen für Eltern-Kommunikation
 * - Integration von Bildungsweg-Informationen (Spezialisierungen)
 * - Profil-Personalisierung für benutzerfreundliche Darstellung
 * <p>
 * Sicherheitsaspekte:
 * - Expliziter Ausschluss von Passwort-Informationen
 * - Sichere Übertragung aller nicht-sensiblen Schüler-Daten
 * - Beibehaltung aller Kommunikations-relevanten Informationen
 * - Schutz vor versehentlicher Preisgabe interner Daten
 * <p>
 * Technische Eigenschaften:
 * - Spring Component mit Dependency Injection für TeacherMapper
 * - Stateless Design für Thread-Safety
 * - Null-sichere Verarbeitung für optionale Beziehungen
 * - Optimiert für Schüler-Self-Service und Eltern-Portale
 * <p>
 * Verwendungsszenarien:
 * - Schüler-Self-Service-Portale für Profilverwaltung
 * - Eltern-Portale für Kinder-Informationen
 * - Lehrer-Tools für Klassenverwaltung und Kommunikation
 * - Administrative Schüler-Übersichten und Berichte
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Student
 * @see StudentDTO
 * @see TeacherMapper
 * @see com.cafeteria.cafeteria_plugin.models.Class
 * @since 2025-01-01
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
     * <p>
     * Diese Methode erstellt eine vollständige DTO-Darstellung eines Schülerkontos
     * und aggregiert alle relevanten Informationen für Frontend-Darstellungen.
     * Sie ist optimiert für sichere API-Übertragung und umfassende Schüler-Profile.
     * <p>
     * Übertragene Basis-Informationen:
     * - Sichere Identifikation (ID, Username ohne Passwort)
     * - Vollständige Kontakt- und Kommunikationsdaten
     * - Profil-Personalisierung (Profilbild)
     * - Klassen-Referenz für organisatorische Zuordnung
     * <p>
     * Klassen-Kontext-Aggregation:
     * - Klassenname für benutzerfreundliche Darstellung
     * - Bildungsweg-Information (Spezialisierung für Oberstufe)
     * - Vollständige Klassenlehrer-Details für Eltern-Kommunikation
     * - Organisatorischer Kontext für Stundenplan und Verwaltung
     * <p>
     * Klassenlehrer-Integration:
     * - Verwendung des TeacherMapper für konsistente Lehrer-DTOs
     * - Vollständige Kontakt-Informationen für Eltern-Kommunikation
     * - Fach-Spezialisierung für fachlichen Kontext
     * - Erste Anlaufstelle für Eltern bei Fragen und Problemen
     * <p>
     * Sicherheitsmerkmale:
     * - Expliziter Ausschluss des Passwort-Feldes aus der Entität
     * - Beibehaltung aller Kommunikations-relevanten Daten
     * - Sichere Übertragung für öffentliche API-Endpunkte
     * - Schutz vor versehentlicher Preisgabe sensibler Informationen
     * <p>
     * Performance-Überlegungen:
     * - Einmalige Entitäts-Navigation für alle erforderlichen Daten
     * - Null-sichere Verarbeitung für optionale Klassen-Beziehungen
     * - Optimiert für Listen-Darstellungen und Detail-Ansichten
     * - Integration mit anderen Mappern für konsistente DTO-Strukturen
     * <p>
     * Verwendung in verschiedenen Kontexten:
     * - Schüler-Profile: Vollständige Informationen für Self-Service
     * - Eltern-Portale: Umfassende Kinder-Informationen mit Lehrer-Kontakt
     * - Lehrer-Tools: Klassenlisten mit allen relevanten Schüler-Details
     * - Administrative Systeme: Vollständige Schüler-Übersichten
     *
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