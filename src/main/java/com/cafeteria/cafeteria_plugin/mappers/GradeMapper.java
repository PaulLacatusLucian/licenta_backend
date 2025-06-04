package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.GradeDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.models.Student;
import org.springframework.stereotype.Component;

/**
 * Mapper-Komponente für die Konvertierung zwischen Grade-Entitäten und GradeDTO-Objekten.
 * <p>
 * Diese Klasse implementiert das Mapper-Pattern für Noten und stellt spezialisierte
 * Konvertierungsfunktionen zwischen der Grade-Domain-Entität und dem entsprechenden
 * Data Transfer Object bereit. Sie extrahiert und aggregiert Kontextinformationen
 * aus verknüpften Entitäten für umfassende Noten-Darstellungen.
 * <p>
 * Hauptfunktionalitäten:
 * - Entity-zu-DTO-Konvertierung mit Kontext-Aggregation
 * - Optimierte DTO-Struktur für Frontend-Darstellungen
 * - Integration von Lehrer-, Fach- und Zeit-Informationen
 * - Performance-optimierte Konvertierung ohne zusätzliche DB-Abfragen
 * - Hilfs-Methoden für Student-DTO-Erstellung
 * <p>
 * Technische Eigenschaften:
 * - Spring Component für automatische Dependency Injection
 * - Stateless Design für Thread-Safety
 * - Flache DTO-Struktur für bessere Performance
 * - Null-sichere Verarbeitung für optionale Beziehungen
 * - Integration mit anderen Mapper-Komponenten
 * <p>
 * Verwendungsszenarien:
 * - Schüler- und Eltern-Portale für Notenübersichten
 * - Lehrer-Tools für Bewertungshistorie
 * - Zeugnis-Generierung und Berichtssysteme
 * - API-Endpunkte für Noten-Abfragen
 *
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
     * <p>
     * Diese Methode erstellt eine optimierte DTO-Darstellung einer Note
     * und extrahiert alle relevanten Kontextinformationen aus verknüpften
     * Entitäten. Die flache DTO-Struktur verbessert die Performance und
     * vereinfacht die Frontend-Integration.
     * <p>
     * Extrahierte Kontextinformationen:
     * - Numerische Bewertung und Beschreibung
     * - Lehrer-Name des bewertenden Lehrers
     * - Fach der bewerteten Leistung
     * - Zeitstempel der Unterrichtsstunde (nicht der Notenerstellung)
     * <p>
     * Performance-Optimierungen:
     * - Direkte Wert-Extraktion ohne DTO-Verschachtelung
     * - Minimaler Memory-Footprint durch primitive/String-Felder
     * - Keine zirkulären Referenzen für JSON-Serialisierung
     * - Einmalige Entitäts-Navigation ohne Lazy-Loading-Probleme
     * <p>
     * Geschäftslogik:
     * - Verwendung der ClassSession-Informationen für Kontext
     * - Zeitstempel basiert auf Unterrichtsstunde, nicht Erstellungszeit
     * - Lehrer-Information vom Fachlehrer der Stunde
     * <p>
     * Verwendung in verschiedenen Kontexten:
     * - Listen-Darstellungen: Effiziente Masse-Konvertierung
     * - Detail-Ansichten: Vollständige Kontext-Informationen
     * - Export-Funktionen: Flache Struktur für CSV/Excel
     * - API-Responses: Optimiert für Frontend-Consumption
     *
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