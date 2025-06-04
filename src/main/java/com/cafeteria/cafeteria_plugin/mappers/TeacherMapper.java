package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import org.springframework.stereotype.Component;

/**
 * Mapper-Komponente für die Konvertierung zwischen Teacher-Entitäten und TeacherDTO-Objekten.
 * <p>
 * Diese Klasse implementiert das Mapper-Pattern für Lehrerkonten und stellt spezialisierte
 * Konvertierungsfunktionen zwischen der Teacher-Domain-Entität und dem entsprechenden
 * Data Transfer Object bereit. Sie behandelt verschiedene Lehrertypen (Educator vs. Teacher)
 * und aggregiert organisatorische Status-Informationen für umfassende Lehrer-Profile.
 * <p>
 * Hauptfunktionalitäten:
 * - Entity-zu-DTO-Konvertierung mit Status-Aggregation
 * - Sichere Datenübertragung ohne Authentifizierungsdaten
 * - Unterscheidung zwischen Educators und Fachlehrern
 * - Klassenzuordnungs-Status für UI-Entscheidungen
 * - Vollständige Kontakt- und Qualifikationsinformationen
 * <p>
 * Lehrertyp-Unterstützung:
 * - Educators: Spezialisiert auf Primarstufenbildung (Klassen 0-4)
 * - Teachers: Fachlehrer für Sekundarstufe (Klassen 5-12)
 * - Klassenlehrer-Status: Zusätzliche Verantwortung für Klassenverwaltung
 * - Flexible Rollenzuordnung je nach Schulorganisation
 * <p>
 * Sicherheitsaspekte:
 * - Expliziter Ausschluss von Passwort-Informationen
 * - Sichere Übertragung aller beruflich relevanten Daten
 * - Beibehaltung von Kontakt- und Qualifikationsinformationen
 * - Schutz vor versehentlicher Preisgabe sensibler Daten
 * <p>
 * Technische Eigenschaften:
 * - Spring Component für automatische Dependency Injection
 * - Statische Methoden für performance-optimierte Aufrufe
 * - Stateless Design für Thread-Safety
 * - Null-sichere Verarbeitung für robuste Konvertierung
 * - Optimiert für häufige Verwendung in anderen Mappern
 * <p>
 * Verwendungsszenarien:
 * - Administrative Lehrer-Verwaltung und Übersichten
 * - Eltern-Portale für Lehrer-Kontaktinformationen
 * - Schüler-Systeme für Unterrichts- und Klassenlehrerinformationen
 * - API-Endpunkte für Lehrer-Profile und Kommunikation
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Teacher
 * @see TeacherDTO
 * @see com.cafeteria.cafeteria_plugin.models.TeacherType
 * @see com.cafeteria.cafeteria_plugin.models.Class
 * @since 2025-01-01
 */
@Component
public class TeacherMapper {

    /**
     * Konvertiert eine Teacher-Entität zu einem umfassenden TeacherDTO.
     * <p>
     * Diese statische Methode erstellt eine vollständige DTO-Darstellung eines Lehrerkontos
     * und aggregiert alle relevanten beruflichen und organisatorischen Informationen.
     * Sie ist optimiert für sichere API-Übertragung und umfassende Lehrer-Profile.
     * <p>
     * Übertragene Basis-Informationen:
     * - Sichere Identifikation (ID, Username ohne Passwort)
     * - Vollständige Kontakt- und Kommunikationsdaten
     * - Fachliche Qualifikation und Spezialisierung
     * - Organisatorischer Status (Klassenzuordnung)
     * <p>
     * Organisatorische Status-Informationen:
     * - hasClassAssigned: Indikator für Klassenlehrer-Funktion
     * - Basis für UI-Entscheidungen in Verwaltungssystemen
     * - Wichtig für Workflow-Steuerung und Berechtigungen
     * - Unterscheidung zwischen Fach- und Klassenlehrern
     * <p>
     * Geschäftslogik-Integration:
     * - Automatische Erkennung der Klassenzuordnung
     * - Unterstützung für verschiedene Lehrertypen
     * - Basis für spezialisierte UI-Funktionen
     * - Integration mit Klassenverwaltungs-Workflows
     * <p>
     * Sicherheitsmerkmale:
     * - Expliziter Ausschluss des Passwort-Feldes
     * - Beibehaltung aller beruflich relevanten Informationen
     * - Sichere Übertragung für öffentliche API-Endpunkte
     * - Schutz vor versehentlicher Preisgabe interner Daten
     * <p>
     * Performance-Überlegungen:
     * - Statische Methode für optimierte Aufrufe
     * - Null-sichere Verarbeitung für optionale Beziehungen
     * - Minimaler Memory-Footprint durch direkte DTO-Erstellung
     * - Optimiert für häufige Verwendung in anderen Mappern
     * <p>
     * Verwendung in verschiedenen Kontexten:
     * - Lehrer-Profile: Vollständige berufliche Informationen
     * - Administrative Systeme: Lehrer-Übersichten mit Status-Information
     * - Eltern-Portale: Kontakt-Informationen für Kommunikation
     * - Verschachtelte DTOs: Integration in Student- und Schedule-DTOs
     *
     * @param teacher Teacher-Entität mit vollständigen Informationen, kann null sein
     * @return TeacherDTO mit aggregierten Informationen oder null bei null-Input
     */
    public static TeacherDTO toDto(Teacher teacher) {
        if (teacher == null) return null;

        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setUsername(teacher.getUsername());
        dto.setEmail(teacher.getEmail());
        dto.setName(teacher.getName());
        dto.setSubject(teacher.getSubject());

        // Organisatorische Status-Information für UI-Entscheidungen
        dto.setHasClassAssigned(teacher.getClassAsTeacher() != null);

        return dto;
    }
}