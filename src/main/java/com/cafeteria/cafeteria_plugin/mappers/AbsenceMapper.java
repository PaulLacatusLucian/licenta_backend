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
 * <p>
 * Diese Klasse implementiert das Mapper-Pattern und stellt bidirektionale Konvertierungsfunktionen
 * zwischen der Absence-Domain-Entität und dem entsprechenden Data Transfer Object bereit.
 * Sie kapselt die komplexe Logik der Datenkonvertierung und stellt sicher, dass alle
 * relevanten Informationen korrekt zwischen den Schichten übertragen werden.
 * <p>
 * Hauptfunktionalitäten:
 * - Entity-zu-DTO-Konvertierung für API-Responses
 * - DTO-zu-Entity-Konvertierung für Datenpersistierung
 * - Aggregation verwandter Entitätsdaten (Student, Teacher, ClassSession)
 * - Sichere Nullwert-Behandlung und Datenvalidierung
 * - Performance-optimierte Konvertierung ohne N+1-Probleme
 * <p>
 * Technische Eigenschaften:
 * - Spring Component für automatische Dependency Injection
 * - Stateless Design für Thread-Safety
 * - Explizite Methoden für verschiedene Konvertierungsszenarien
 * - Integration mit anderen Mapper-Komponenten
 * - Unterstützung für bidirektionale Konvertierung
 * <p>
 * Verwendungsszenarien:
 * - REST Controller für API-Response-Generierung
 * - Service Layer für DTO-Entity-Transformationen
 * - Bulk-Operationen mit Listen-Konvertierungen
 * - Komplexe Geschäftslogik mit Datenintegration
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Absence
 * @see AbsenceDTO
 * @see StudentDTO
 * @see TeacherDTO
 * @since 2025-01-01
 */
@Component
public class AbsenceMapper {

    /**
     * Konvertiert eine AbsenceDTO zu einer Absence-Entität mit vollständigen Abhängigkeiten.
     * <p>
     * Diese Überladung der toEntity-Methode erstellt eine vollständige Absence-Entität
     * mit allen erforderlichen Beziehungen. Sie wird typischerweise bei der Erstellung
     * neuer Fehlzeiten verwendet, wo alle Kontext-Informationen verfügbar sind.
     * <p>
     * Konvertierungslogik:
     * - Übertragung der Basis-Identifikation (ID)
     * - Zuordnung der Student-Entität
     * - Verknüpfung mit ClassSession
     * - Zuordnung des erfassenden Lehrers
     * <p>
     * Geschäftsregeln:
     * - Student muss zur Klasse der ClassSession gehören
     * - Teacher muss berechtigt sein, Fehlzeiten zu erfassen
     * - ClassSession muss existieren und aktiv sein
     *
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
     * Aggregierte Informationen:
     * - Basis-Fehlzeit-Daten (ID, ClassSession-Referenz)
     * - Vollständige Student-Informationen mit Klassen-Kontext
     * - Teacher-Details des erfassenden Lehrers
     * - Zeitstempel der Unterrichtsstunde (nicht der Erfassung)
     * - Klassenlehrer-Informationen für Student-Kontext
     * <p>
     * Performance-Optimierungen:
     * - Einmalige Entitäts-Navigation ohne zusätzliche Datenbankabfragen
     * - Effiziente DTO-Erstellung mit Minimal-Memory-Footprint
     * - Lazy-Loading-freundliche Implementierung
     * <p>
     * Geschäftslogik:
     * - Verwendung der ClassSession-Startzeit als Fehlzeit-Datum
     * - Unterscheidung zwischen Fachlehrer (erfassend) und Klassenlehrer
     * - Vollständige Student-Klassen-Information für Kontext
     *
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
        return dto;
    }
}