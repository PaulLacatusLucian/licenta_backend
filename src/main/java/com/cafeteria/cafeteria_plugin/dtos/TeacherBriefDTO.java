package com.cafeteria.cafeteria_plugin.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Minimale Data Transfer Object-Darstellung für Lehrer-Informationen.
 * <p>
 * Diese Klasse stellt eine stark reduzierte, effiziente Darstellung von
 * Lehrer-Informationen dar, die für spezifische Anwendungsfälle optimiert ist,
 * bei denen nur grundlegende Kontakt- und Identifikationsdaten benötigt werden.
 * Sie minimiert die Datenübertragung und verbessert die Performance.
 * <p>
 * Das DTO enthält:
 * - Grundlegende Identifikationsinformationen
 * - Primäres Unterrichtsfach
 * - Direkte Kontaktmöglichkeit
 * - Minimale Datenstruktur für Effizienz
 * <p>
 * Verwendungsszenarien:
 * - Eltern-Portale für Lehrerlisten
 * - Kontakt-Übersichten und Verzeichnisse
 * - Mobile Apps mit begrenzter Bandbreite
 * - Schnelle Lehrer-Suche und -Auswahl
 * - Kommunikations-Interfaces mit Fokus auf Kontakt
 * <p>
 * Technische Eigenschaften:
 * - Minimale Datenübertragung für bessere Performance
 * - Explicit Getter/Setter für präzise Kontrolle
 * - JSON-serialisierbar für REST APIs
 * - Optimiert für Listen-Darstellungen
 * - Reduzierte Speicher-Footprint
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see TeacherDTO
 * @see com.cafeteria.cafeteria_plugin.models.Teacher
 * @see com.cafeteria.cafeteria_plugin.controllers.ParentController
 * @since 2025-04-22
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherBriefDTO {

    /**
     * Vollständiger Name des Lehrers.
     * <p>
     * Vor- und Nachname für persönliche Identifikation:
     * - Anzeige in Kontaktlisten
     * - Benutzerfreundliche Lehrer-Identifikation
     * - Basis für alphabetische Sortierung
     * - Wichtig für Eltern-Kommunikation
     * <p>
     * Verwendung in UI:
     * - Dropdown-Listen für Lehrer-Auswahl
     * - Kontaktverzeichnisse
     * - Schnellreferenz-Listen
     * - E-Mail-Empfänger-Auswahl
     */
    private String name;

    /**
     * Primäres Unterrichtsfach des Lehrers.
     * <p>
     * Hauptfach oder Spezialisierung:
     * - Fachliche Zuordnung für Eltern
     * - Kontext für fachspezifische Fragen
     * - Filterkriterium in Lehrerlisten
     * - Wichtig für themenbezogene Kommunikation
     * <p>
     * Beispiele: "Mathematik", "Deutsch", "Biologie",
     * "Englisch", "Geschichte", "Informatik"
     * <p>
     * Hinweis: Bei Lehrern mit mehreren Fächern
     * wird das Hauptfach oder häufigste Fach angezeigt.
     */
    private String subject;

    /**
     * E-Mail-Adresse für direkte Kommunikation.
     * <p>
     * Primäre Kontakt-E-Mail des Lehrers:
     * - Direkte Eltern-Lehrer-Kommunikation
     * - E-Mail-System-Integration
     * - Automatische Benachrichtigungen
     * - Offizielle Korrespondenz
     * <p>
     * Verwendung:
     * - Eltern-Nachrichten über Portal
     * - Terminvereinbarungen
     * - Fachspezifische Anfragen
     * - Automatische E-Mail-Weiterleitung
     * <p>
     * Sicherheit: Diese E-Mail wird für Portal-interne
     * Kommunikation verwendet und validiert.
     */
    private String email;
}