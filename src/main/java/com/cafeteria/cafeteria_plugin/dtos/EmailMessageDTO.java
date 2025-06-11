package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

/**
 * Data Transfer Object für E-Mail-Nachrichten zwischen Eltern und Lehrern.
 * <p>
 * Diese Klasse kapselt alle notwendigen Informationen für die Übertragung
 * von E-Mail-Nachrichten im Schulsystem. Sie ermöglicht strukturierte
 * Kommunikation zwischen Eltern und Lehrern über das System-Portal und
 * stellt sicher, dass alle erforderlichen Informationen vorhanden sind.
 * <p>
 * Das DTO enthält:
 * - Empfänger-Identifikation (Lehrer-E-Mail)
 * - Betreff der Nachricht
 * - Vollständigen Nachrichteninhalt
 * - Minimale Struktur für effiziente Übertragung
 * <p>
 * Verwendungsszenarien:
 * - Eltern-Lehrer-Kommunikation über Portal
 * - REST API Requests für Nachrichtenversand
 * - Mobile Apps für Schul-Kommunikation
 * - Strukturierte E-Mail-Weiterleitung
 * - Nachrichtenverfolgung und -archivierung
 * <p>
 * Technische Eigenschaften:
 * - Einfache, flache Datenstruktur
 * - JSON-serialisierbar für REST APIs
 * - Validierbar durch Bean Validation
 * - Lombok-Annotations für automatische Getter/Setter
 * - Optimiert für E-Mail-Service-Integration
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see com.cafeteria.cafeteria_plugin.email.EmailService
 * @see com.cafeteria.cafeteria_plugin.controllers.ParentController
 * @since 2025-04-22
 */
@Data
public class EmailMessageDTO {

    /**
     * E-Mail-Adresse des Empfänger-Lehrers.
     * <p>
     * Ziel-E-Mail-Adresse für die Nachricht:
     * - Eindeutige Identifikation des Empfängers
     * - Validierung gegen Lehrer-Datenbank
     * - Basis für E-Mail-Routing
     * - Sicherstellung der korrekten Zustellung
     * <p>
     * Validierung:
     * - Muss gültige E-Mail-Format haben
     * - Sollte existierender Lehrer im System sein
     * - Prüfung auf Berechtigung für Eltern-Kommunikation
     * - Spam-Schutz durch Validierung
     */
    private String teacherEmail;

    /**
     * Betreff der E-Mail-Nachricht.
     * <p>
     * Kurze Zusammenfassung des Nachrichteninhalts:
     * - Pflichtfeld für strukturierte Kommunikation
     * - Basis für E-Mail-Filterung und -Organisation
     * - Wichtig für Nachrichtenverfolgung
     * - Hilft bei Prioritätensetzung
     * <p>
     * Best Practices:
     * - Klare, beschreibende Betreffzeilen
     * - Vermeidung von Spam-verdächtigen Begriffen
     * - Klassifikation des Nachrichtentyps
     * - Kurz aber informativ
     */
    private String subject;

    /**
     * Vollständiger Inhalt der E-Mail-Nachricht.
     * <p>
     * Detaillierte Nachricht von Eltern an Lehrer:
     * - Hauptinhalt der Kommunikation
     * - Kann formatiert oder Plain-Text sein
     * - Basis für E-Mail-Body-Generierung
     * - Archivierung für spätere Referenz
     * <p>
     * Verwendung:
     * - Fragen zu Schulleistungen
     * - Terminvereinbarungen
     * - Informationen über Schüler-Situationen
     * - Feedback und Anregungen
     * - Formale Anträge und Mitteilungen
     */
    private String content;
}