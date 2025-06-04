package com.cafeteria.cafeteria_plugin.meetings;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Konfigurationsklasse für Google Calendar API-Authentifizierungsparameter.
 * <p>
 * Diese Klasse implementiert das Spring Boot Configuration Properties Pattern
 * und kapselt alle erforderlichen OAuth2-Authentifizierungsparameter für die
 * Integration mit der Google Calendar API. Sie ermöglicht externalisierte
 * Konfiguration durch application.properties oder Umgebungsvariablen.
 * <p>
 * Die Klasse verwaltet:
 * - OAuth2 Client-Identifikation für Google API-Zugriff
 * - Refresh Token für dauerhafte API-Berechtigung
 * - Sichere Konfigurationsverwaltung ohne Hardcoding
 * - Flexible Deployment-Konfiguration für verschiedene Umgebungen
 * <p>
 * Sicherheitsaspekte:
 * - Verwendung von Refresh Tokens für langfristige Authentifizierung
 * - Externalisierte Konfiguration verhindert Credential-Exposition im Code
 * - Unterstützung für umgebungsbasierte Konfiguration (Dev/Test/Prod)
 * - Sichere Token-Verwaltung durch Spring's Configuration-System
 * <p>
 * OAuth2-Workflow-Integration:
 * - Client ID und Secret für Google OAuth2-Anwendungsregistrierung
 * - Refresh Token für automatische Access Token-Erneuerung
 * - Eliminierung manueller Re-Authentifizierung
 * - Unterstützung für Server-zu-Server-Kommunikation
 * <p>
 * Konfigurationsquellen:
 * - application.properties: google.calendar.client-id, client-secret, refresh-token
 * - Umgebungsvariablen: GOOGLE_CALENDAR_CLIENT_ID, etc.
 * - Externe Konfigurationsdateien für sichere Produktions-Deployments
 * - Spring Cloud Config für zentralisierte Konfigurationsverwaltung
 * <p>
 * Verwendungsszenarien:
 * - Schulleiter-Tools für automatische Elternabend-Terminierung
 * - Lehrer-Systeme für Konferenz-Scheduling mit Eltern
 * - Administrative Planungstools für Schul-Events
 * - Integration mit bestehenden Schul-Kalendersystemen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see GoogleCalendarService
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @see com.google.api.client.auth.oauth2.TokenResponse
 * @since 2025-01-01
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "google.calendar")
public class GoogleCalendarProperties {

    /**
     * Google OAuth2 Client-ID für API-Authentifizierung.
     * <p>
     * Eindeutige Identifikation der registrierten Google Cloud Console-Anwendung.
     * Diese ID identifiziert die Schul-Anwendung gegenüber Google's OAuth2-System
     * und ermöglicht autorisierte API-Zugriffe.
     * <p>
     * Konfiguration:
     * - Property: google.calendar.client-id
     * - Umgebungsvariable: GOOGLE_CALENDAR_CLIENT_ID
     * - Format: OAuth2 Client-ID von Google Cloud Console
     * <p>
     * Sicherheitshinweise:
     * - Öffentlicher Identifier, aber sollte nicht frei zugänglich sein
     * - Spezifisch für die registrierte Schul-Anwendung
     * - Muss mit Google Cloud Console-Registrierung übereinstimmen
     */
    private String clientId;

    /**
     * Google OAuth2 Client-Secret für sichere Authentifizierung.
     * <p>
     * Geheimer Authentifizierungsschlüssel der registrierten Google Cloud Console-Anwendung.
     * Dieser Secret wird zusammen mit der Client-ID für sichere OAuth2-Token-Anfragen
     * verwendet und muss streng vertraulich behandelt werden.
     */
    private String clientSecret;

    /**
     * Google OAuth2 Refresh Token für dauerhafte API-Berechtigung.
     * <p>
     * Langlebiger Token, der automatische Erneuerung von Access Tokens ermöglicht
     * ohne erneute manuelle Benutzer-Authentifizierung. Ermöglicht Server-zu-Server
     * API-Zugriffe für Kalender-Operationen im Namen des autorisierten Benutzers.
     * <p>
     * Token-Lifecycle:
     * - Einmalige Generierung durch OAuth2-Autorisierungsflow
     * - Langfristige Gültigkeit (bis zur expliziten Revokation)
     * - Automatische Access Token-Generierung durch GoogleCalendarService
     * - Eliminiert wiederkehrende manuelle Authentifizierung
     */
    private String refreshToken;
}