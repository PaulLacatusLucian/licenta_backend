package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

/**
 * Data Transfer Object für Eltern-Informationen.
 * <p>
 * Diese Klasse kapselt alle relevanten Informationen über Elternkonten
 * für die Übertragung zwischen verschiedenen Anwendungsschichten. Sie stellt eine
 * sichere, serialisierbare Darstellung der Parent-Entität dar und
 * schließt sensible Daten wie Passwörter aus der Übertragung aus.
 * <p>
 * Das DTO enthält:
 * - Grundlegende Konto-Identifikation
 * - Vollständige Kontaktinformationen beider Elternteile
 * - Dual-Email-System für flexible Kommunikation
 * - Profil-Informationen für Personalisierung
 * - Sichere Darstellung ohne Authentifizierungsdaten
 * <p>
 * Verwendungsszenarien:
 * - REST API Responses für Eltern-Profile
 * - Eltern-Self-Service-Portale
 * - Lehrer-Tools für Eltern-Kommunikation
 * - Administrative Eltern-Verwaltung
 * - Kontaktlisten und Verzeichnisse
 * <p>
 * Technische Eigenschaften:
 * - Sicherheits-optimiert ohne sensible Daten
 * - JSON-serialisierbar für REST APIs
 * - Vollständige Kontakt-Informationen
 * - Lombok-Annotations für automatische Getter/Setter
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see com.cafeteria.cafeteria_plugin.models.Parent
 * @see StudentDTO
 * @see com.cafeteria.cafeteria_plugin.models.User
 * @since 2025-01-01
 */
@Data
public class ParentDTO {

    /**
     * Eindeutige Identifikation des Elternkontos.
     * <p>
     * Primärschlüssel der Parent-Entität:
     * - Referenz für API-Operationen
     * - Verknüpfung zu Schüler-Konten
     * - Basis für Berechtigungsprüfungen
     * - Eindeutige Identifikation im System
     */
    private Long id;

    /**
     * Haupt-E-Mail-Adresse des Elternkontos.
     * <p>
     * Primäre E-Mail für Systemkommunikation:
     * - Login-Identifikation
     * - Haupt-Kommunikationskanal
     * - Passwort-Reset-Ziel
     * - System-Benachrichtigungen
     * <p>
     * Hinweis: Typischerweise identisch mit motherEmail,
     * kann aber bei Bedarf separat verwaltet werden.
     */
    private String email;

    /**
     * Benutzername für System-Anmeldung.
     * <p>
     * Eindeutige Anmelde-Kennung:
     * - Alternative zur E-Mail-Anmeldung
     * - Benutzerfreundliche Identifikation
     * - System-interne Referenz
     * - Display-Name in Benutzeroberflächen
     */
    private String username;

    /**
     * Vollständiger Name der Mutter.
     * <p>
     * Vor- und Nachname der Mutter:
     * - Persönliche Identifikation
     * - Kontakt-Verwaltung
     * - Elternabend-Listen
     * - Offizielle Korrespondenz
     */
    private String motherName;

    /**
     * E-Mail-Adresse der Mutter.
     * <p>
     * Spezifische Kontakt-E-Mail der Mutter:
     * - Direkte Kommunikation mit Mutter
     * - Getrennte Benachrichtigungen
     * - Flexible Familien-Kommunikation
     * - Backup-Kontakt-Option
     */
    private String motherEmail;

    /**
     * Telefonnummer der Mutter.
     * <p>
     * Direkte Kontaktmöglichkeit:
     * - Notfall-Kontakt
     * - Schnelle Kommunikation
     * - SMS-Benachrichtigungen
     * - Telefon-Konferenzen und -Gespräche
     */
    private String motherPhoneNumber;

    /**
     * Vollständiger Name des Vaters.
     * <p>
     * Vor- und Nachname des Vaters:
     * - Persönliche Identifikation
     * - Kontakt-Verwaltung
     * - Elternabend-Listen
     * - Offizielle Korrespondenz
     */
    private String fatherName;

    /**
     * E-Mail-Adresse des Vaters.
     * <p>
     * Spezifische Kontakt-E-Mail des Vaters:
     * - Direkte Kommunikation mit Vater
     * - Getrennte Benachrichtigungen
     * - Flexible Familien-Kommunikation
     * - Alternative Kontakt-Option
     */
    private String fatherEmail;

    /**
     * Telefonnummer des Vaters.
     * <p>
     * Direkte Kontaktmöglichkeit:
     * - Notfall-Kontakt
     * - Schnelle Kommunikation
     * - SMS-Benachrichtigungen
     * - Telefon-Konferenzen und -Gespräche
     */
    private String fatherPhoneNumber;

    /**
     * URL zum Profilbild des Elternkontos.
     * <p>
     * Relativer Pfad zum hochgeladenen Profilbild:
     * - Personalisierung der Benutzeroberfläche
     * - Visuelle Identifikation
     * - Benutzerfreundlichkeit
     * - Social Features im Portal
     * <p>
     * Format: "/images/[filename]" für lokale Speicherung
     */
    private String profileImage;
}