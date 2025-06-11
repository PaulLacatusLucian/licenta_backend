package com.cafeteria.cafeteria_plugin.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object für Schüler-Informationen.
 * <p>
 * Diese Klasse kapselt alle relevanten Informationen über Schülerkonten
 * für die Übertragung zwischen verschiedenen Anwendungsschichten. Sie stellt eine
 * sichere, serialisierbare Darstellung der Student-Entität dar und
 * integriert Klassen- und Lehrer-Informationen für vollständige Schüler-Profile.
 * <p>
 * Das DTO enthält:
 * - Grundlegende Schüler-Identifikation
 * - Kontaktinformationen und Kommunikationsdaten
 * - Vollständige Klassen- und Lehrerkontext
 * - Profil-Informationen für Personalisierung
 * - Sichere Darstellung ohne Authentifizierungsdaten
 * <p>
 * Verwendungsszenarien:
 * - REST API Responses für Schüler-Profile
 * - Lehrer-Tools für Klassenverwaltung
 * - Eltern-Portale für Kinder-Informationen
 * - Administrative Schüler-Verwaltung
 * - Klassenbuch- und Noten-Systeme
 * <p>
 * Technische Eigenschaften:
 * - Immutable Design mit All/NoArgsConstructor
 * - JSON-serialisierbar für REST APIs
 * - Optimiert für Frontend-Darstellungen
 * - Sichere Datenübertragung ohne sensible Informationen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see com.cafeteria.cafeteria_plugin.models.Student
 * @see TeacherDTO
 * @see com.cafeteria.cafeteria_plugin.models.Class
 * @since 2025-03-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {

    /**
     * Eindeutige Identifikation des Schülerkontos.
     * <p>
     * Primärschlüssel der Student-Entität:
     * - Referenz für alle schülerbezogenen Operationen
     * - Verknüpfung zu Noten, Fehlzeiten und Bestellungen
     * - API-Parameter für schülerspezifische Endpunkte
     * - Basis für Berechtigungsprüfungen
     */
    private Long id;

    /**
     * Benutzername für System-Anmeldung.
     * <p>
     * Eindeutige Anmelde-Kennung des Schülers:
     * - Login-Identifikation für Schüler-Portal
     * - System-interne Referenz
     * - Display-Name in Benutzeroberflächen
     * - JWT-Token-Basis für Authentifizierung
     */
    private String username;

    /**
     * Vollständiger Name des Schülers.
     * <p>
     * Vor- und Nachname für persönliche Identifikation:
     * - Anzeige in allen Benutzeroberflächen
     * - Klassenlisten und Anwesenheitskontrolle
     * - Eltern-Kommunikation und Berichte
     * - Offizielle Dokumente und Zeugnisse
     */
    private String name;

    /**
     * Telefonnummer des Schülers.
     * <p>
     * Direkte Kontaktmöglichkeit:
     * - Notfall-Kontakt bei Schulproblemen
     * - SMS-Benachrichtigungen und Erinnerungen
     * - Kommunikation für außerschulische Aktivitäten
     * - Backup-Kontakt wenn Eltern nicht erreichbar
     */
    private String phoneNumber;

    /**
     * E-Mail-Adresse des Schülers.
     * <p>
     * Schüler-spezifische E-Mail für System-Kommunikation:
     * - Login-Alternative zum Benutzernamen
     * - Direktes Feedback zu Noten und Leistungen
     * - Hausaufgaben- und Terminbenachrichtigungen
     * - Digitale Kommunikation mit Lehrern
     */
    private String email;

    /**
     * ID der zugewiesenen Schulklasse.
     * <p>
     * Referenz zur Class-Entität:
     * - Organisatorische Zuordnung
     * - Basis für klassenspezifische Operationen
     * - Stundenplan- und Lehrerzuordnung
     * - Klassenbuch-Integration
     */
    private Long classId;

    /**
     * URL zum Profilbild des Schülers.
     * <p>
     * Relativer Pfad zum hochgeladenen Profilbild:
     * - Personalisierung der Benutzeroberfläche
     * - Visuelle Identifikation in Listen
     * - Benutzerfreundlichkeit und Wiedererkennungswert
     * - Social Features im Schüler-Portal
     * <p>
     * Format: "/images/[filename]" für lokale Speicherung
     */
    private String profileImage;

    /**
     * Name der zugewiesenen Schulklasse.
     * <p>
     * Benutzerfreundliche Klassenbezeichnung:
     * - Anzeige in Benutzeroberflächen
     * - Schnelle Klassenidentifikation
     * - Eltern-Kommunikation und Berichte
     * - Organisatorische Übersichten
     * <p>
     * Beispiele: "10A", "9B", "12C"
     */
    private String className;

    /**
     * Spezialisierung der Schulklasse (Oberstufe).
     * <p>
     * Fachrichtung für Gymnasial-Oberstufe:
     * - Wichtig für Kurswahl und Fächerangebot
     * - Zeugnis- und Abschluss-relevante Information
     * - Orientierung für Berufswahl
     * - Eltern-Information über Bildungsweg
     * <p>
     * Beispiele: "Mathematik-Informatik", "Biologie-Chemie",
     * "Sprachen", "Gesellschaftswissenschaften"
     * <p>
     * Hinweis: Nur für Oberstufen-Klassen relevant (Klassen 9-12)
     */
    private String classSpecialization;

    /**
     * Vollständige Klassenlehrer-Informationen als verschachtelte DTO.
     * <p>
     * Enthält alle relevanten Klassenlehrer-Daten:
     * - Name, Fach, Kontaktinformationen
     * - Hauptansprechpartner für Eltern
     * - Verantwortlich für Klassenverwaltung
     * - Wichtig für Eltern-Lehrer-Kommunikation
     * <p>
     * Der Klassenlehrer ist verantwortlich für:
     * - Allgemeine Klassenverwaltung und -organisation
     * - Erste Anlaufstelle bei Problemen
     * - Zeugnis-Koordination und Beratung
     * - Elternabende und Klassenpflegschaft
     */
    private TeacherDTO classTeacher;
}