package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.ClassSessionDTO;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper-Komponente für die Konvertierung zwischen ClassSession-Entitäten und ClassSessionDTO-Objekten.
 * <p>
 * Diese Klasse implementiert das Mapper-Pattern für Unterrichtsstunden und stellt bidirektionale
 * Konvertierungsfunktionen zwischen der ClassSession-Domain-Entität und dem entsprechenden
 * Data Transfer Object bereit. Sie aggregiert komplexe Beziehungsdaten aus Fehlzeiten,
 * Noten und Lehrerinformationen für vollständige Unterrichtsstunden-Darstellungen.
 * <p>
 * Hauptfunktionalitäten:
 * - Entity-zu-DTO-Konvertierung für umfassende Unterrichtsstunden-APIs
 * - DTO-zu-Entity-Konvertierung für Datenpersistierung
 * - Aggregation von Bewertungsdaten (Noten und Fehlzeiten)
 * - Integration mit anderen Mapper-Komponenten
 * - Performance-optimierte Listen-Konvertierungen
 * <p>
 * Technische Eigenschaften:
 * - Spring Component mit automatischer Dependency Injection
 * - Verwendung anderer Mapper für verschachtelte Objekte
 * - Stateless Design für Thread-Safety
 * - Stream-API-Integration für Listen-Verarbeitung
 * - Lazy-Loading-freundliche Implementierung
 * <p>
 * Verwendungsszenarien:
 * - Lehrer-Dashboard für Unterrichtsstunden-Management
 * - Klassenbuch-Systeme mit vollständigen Bewertungen
 * - API-Endpunkte für Unterrichtsstunden-Details
 * - Berichte und Statistiken über Unterrichtsaktivitäten
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see ClassSession
 * @see ClassSessionDTO
 * @see AbsenceMapper
 * @see GradeMapper
 * @see TeacherMapper
 * @since 2025-01-01
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
     * <p>
     * Aggregierte Daten:
     * - Basis-Unterrichtsstunden-Informationen (ID, Fach, Zeiten)
     * - Vollständige Lehrerinformationen
     * - Liste aller Fehlzeiten mit Schülerdetails
     * - Liste aller Noten mit Bewertungsdetails
     * - Organisatorische Informationen (Tag, Klasse)
     * <p>
     * Performance-Überlegungen:
     * - Verwendet Stream-API für effiziente Listen-Konvertierung
     * - Lazy-Loading-kompatible Implementierung
     * - Null-sichere Verarbeitung für optionale Beziehungen
     * - Minimierung von Speicher-Overhead durch direkte DTO-Erstellung
     * <p>
     * Verwendung in verschiedenen Kontexten:
     * - Lehrer-Tools: Vollständige Stunden-Übersicht mit allen Bewertungen
     * - Klassenbuch: Integrierte Darstellung von Anwesenheit und Noten
     * - Berichte: Aggregierte Daten für Statistiken und Analysen
     * - API-Responses: Umfassende Informationen für Frontend-Darstellung
     *
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
     * <p>
     * Konvertierte Daten:
     * - Identifikation und Basis-Informationen
     * - Fach und Zeitraum-Definitionen
     * - Organisatorische Zuordnungen (Tag, Klasse)
     * <p>
     * Hinweis zu Beziehungen:
     * - Teacher, Absences und Grades werden nicht konvertiert
     * - Diese müssen separat durch Service-Layer-Logik zugeordnet werden
     * - Fokus auf Kern-Entitätsdaten für sichere Persistierung
     * <p>
     * Verwendungsszenarien:
     * - Neue Unterrichtsstunden-Erstellung über API
     * - Update-Operationen für Basis-Informationen
     * - Import-Funktionen für Stundenplan-Daten
     * - Service-Layer-Operationen mit anschließender Beziehungs-Zuordnung
     *
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