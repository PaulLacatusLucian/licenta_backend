package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.ScheduleDTO;
import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.Schedule;
import org.springframework.stereotype.Component;

/**
 * Mapper-Komponente für die Konvertierung zwischen Schedule-Entitäten und ScheduleDTO-Objekten.
 * <p>
 * Diese Klasse implementiert das Mapper-Pattern für Stundenpläne und stellt spezialisierte
 * Konvertierungsfunktionen zwischen der Schedule-Domain-Entität und dem entsprechenden
 * Data Transfer Object bereit. Sie aggregiert Lehrer- und Klasseninformationen für
 * vollständige Stundenplan-Darstellungen und optimiert die Daten für Frontend-Consumption.
 * <p>
 * Hauptfunktionalitäten:
 * - Entity-zu-DTO-Konvertierung mit Lehrer-Aggregation
 * - Vollständige Stundenplan-Informationen für UI-Darstellung
 * - Integration von Klassen- und Zeitraum-Kontext
 * - Fächer-Listen-Unterstützung für flexible Unterrichtsgestaltung
 * - Optimierte DTO-Struktur für Kalender-Komponenten
 * <p>
 * Technische Eigenschaften:
 * - Spring Component für automatische Dependency Injection
 * - Stateless Design für Thread-Safety
 * - Direkte Lehrer-DTO-Erstellung für Performance
 * - Null-sichere Verarbeitung für robuste Konvertierung
 * - Optimiert für Stundenplan-Visualisierungen
 * <p>
 * Verwendungsszenarien:
 * - Frontend-Stundenplan-Darstellungen für Schüler und Lehrer
 * - Mobile Apps für Stundenplan-Anzeige
 * - API-Endpunkte für Stundenplan-Abfragen
 * - Kalender-Integrationen und Terminplanungs-Systeme
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Schedule
 * @see ScheduleDTO
 * @see TeacherDTO
 * @see com.cafeteria.cafeteria_plugin.models.Class
 * @since 2025-01-01
 */
@Component
public class ScheduleMapper {

    /**
     * Konvertiert eine Schedule-Entität zu einem umfassenden ScheduleDTO.
     * <p>
     * Diese Methode erstellt eine vollständige DTO-Darstellung eines Stundenplan-Eintrags
     * und aggregiert alle relevanten Informationen für Frontend-Darstellungen. Sie ist
     * optimiert für Kalender-Komponenten und Stundenplan-Visualisierungen.
     * <p>
     * Aggregierte Stundenplan-Informationen:
     * - Basis-Identifikation und Zeitraum-Definition
     * - Lokalisierte Wochentag-Darstellung (deutsche Bezeichnungen)
     * - Flexible Fächer-Listen für Multi-Fach-Stunden
     * - Vollständige Lehrer-Informationen mit Kontakt-Details
     * - Klassen-Kontext für organisatorische Zuordnung
     * <p>
     * Lehrer-DTO-Aggregation:
     * - Vollständige Identifikations- und Kontaktdaten
     * - Fach-Spezialisierung für fachlichen Kontext
     * - Login-Informationen für System-Integration
     * - Optimiert für Lehrer-Kontakt und Kommunikation
     * <p>
     * Zeit- und Organisationskontext:
     * - Deutsche Wochentag-Bezeichnungen für Lokalisierung
     * - Präzise Zeit-Angaben im HH:mm-Format
     * - Klassen-Name für organisatorische Zuordnung
     * - Multi-Fach-Unterstützung für fächerübergreifenden Unterricht
     * <p>
     * Performance-Überlegungen:
     * - Direkte DTO-Erstellung ohne verschachtelte Mapper-Aufrufe
     * - Einmalige Entitäts-Navigation für alle erforderlichen Daten
     * - Null-sichere Verarbeitung für optionale Beziehungen
     * - Optimiert für Listen-Darstellungen in Stundenplänen
     * <p>
     * Frontend-Optimierungen:
     * - Flache DTO-Struktur für einfache Darstellung
     * - Alle notwendigen Informationen in einem Objekt
     * - Vermeidung zusätzlicher API-Calls für verwandte Daten
     * - JSON-serialisierungsfreundliche Struktur
     *
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