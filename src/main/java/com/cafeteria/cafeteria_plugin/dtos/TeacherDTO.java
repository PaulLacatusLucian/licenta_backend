package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

/**
 * Data Transfer Object für vollständige Lehrer-Informationen.
 * <p>
 * Diese Klasse kapselt alle relevanten Informationen über Lehrerkonten
 * für die Übertragung zwischen verschiedenen Anwendungsschichten. Sie stellt eine
 * sichere, serialisierbare Darstellung der Teacher-Entität dar und
 * schließt sensible Daten wie Passwörter aus der Übertragung aus.
 * <p>
 * Das DTO enthält:
 * - Grundlegende Lehrer-Identifikation
 * - Kontakt- und Kommunikationsinformationen
 * - Fachliche Zuordnung und Spezialisierung
 * - Organisatorische Status-Informationen
 * - Sichere Darstellung ohne Authentifizierungsdaten
 * <p>
 * Verwendungsszenarien:
 * - REST API Responses für Lehrer-Profile
 * - Administrative Lehrer-Verwaltung
 * - Eltern-Portale für Lehrerübersichten
 * - Schüler-Systeme für Unterrichtsinformationen
 * - Stundenplan- und Klassenzuordnungen
 * <p>
 * Technische Eigenschaften:
 * - Sicherheits-optimiert ohne sensible Daten
 * - JSON-serialisierbar für REST APIs
 * - Status-Informationen für UI-Entscheidungen
 * - Lombok-Annotations für automatische Getter/Setter
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see com.cafeteria.cafeteria_plugin.models.Teacher
 * @see com.cafeteria.cafeteria_plugin.models.Class
 * @see TeacherBriefDTO
 * @since 2025-03-13
 */
@Data
public class TeacherDTO {

    /**
     * Eindeutige Identifikation des Lehrerkontos.
     * <p>
     * Primärschlüssel der Teacher-Entität:
     * - Referenz für alle lehrerbezogenen Operationen
     * - Verknüpfung zu Unterrichtsstunden und Bewertungen
     * - API-Parameter für lehrerspezifische Endpunkte
     * - Basis für Berechtigungsprüfungen
     */
    private Long id;

    /**
     * Benutzername für System-Anmeldung.
     * <p>
     * Eindeutige Anmelde-Kennung des Lehrers:
     * - Login-Identifikation für Lehrer-Portal
     * - System-interne Referenz
     * - Display-Name in Benutzeroberflächen
     * - JWT-Token-Basis für Authentifizierung
     */
    private String username;

    /**
     * E-Mail-Adresse für Kommunikation und Anmeldung.
     * <p>
     * Primäre E-Mail des Lehrers:
     * - Alternative Login-Option
     * - Haupt-Kommunikationskanal
     * - Eltern-Lehrer-Korrespondenz
     * - System-Benachrichtigungen
     * - Passwort-Reset-Ziel
     */
    private String email;

    /**
     * Vollständiger Name des Lehrers.
     * <p>
     * Vor- und Nachname für persönliche Identifikation:
     * - Anzeige in allen Benutzeroberflächen
     * - Klassenlisten und Unterrichtspläne
     * - Eltern-Kommunikation und offizielle Dokumente
     * - Stundenplan-Darstellung und Vertretungspläne
     */
    private String name;

    /**
     * Primäres Unterrichtsfach oder Spezialisierung.
     * <p>
     * Hauptfach des Lehrers:
     * - Fachliche Zuordnung und Qualifikation
     * - Basis für Unterrichtszuteilung
     * - Wichtig für fachspezifische Kommunikation
     * - Filterkriterium in Lehrerübersichten
     * <p>
     * Beispiele: "Mathematik", "Deutsch", "Biologie",
     * "Englisch", "Geschichte", "Informatik", "Învățător" (für Educators)
     * <p>
     * Sonderfälle:
     * - Educators (Primarstufe): oft "Învățător" als Generalist
     * - Fachlehrer (Sekundarstufe): spezifisches Fach
     * - Klassenlehrer: Hauptfach + Klassenleitungsfunktion
     */
    private String subject;

    /**
     * Status der Klassenzuteilung.
     * <p>
     * Indikator ob der Lehrer eine Klasse als Klassenlehrer betreut:
     * - true: Lehrer ist Klassenlehrer einer Klasse
     * - false: Lehrer ist nur Fachlehrer
     * <p>
     * Verwendung:
     * - UI-Entscheidungen für Klassenverwaltungs-Features
     * - Berechtigungsprüfung für klassenspezifische Funktionen
     * - Administrative Übersichten über Klassenzuteilungen
     * - Workflow-Steuerung in Verwaltungssystemen
     * <p>
     * Geschäftsregeln:
     * - Jeder Lehrer kann maximal eine Klasse als Klassenlehrer betreuen
     * - Educators sind typischerweise Klassenlehrer für Primarklassen
     * - Teachers können Klassenlehrer für Sekundarklassen sein
     */
    private boolean hasClassAssigned;
}