package com.cafeteria.cafeteria_plugin.meetings;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Konfigurationsklasse für Google Calendar API-Authentifizierungsparameter.
 * @author Paul Lacatus
 * @version 1.0
 * @see GoogleCalendarService
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @see com.google.api.client.auth.oauth2.TokenResponse
 * @since 2025-04-09
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "google.calendar")
public class GoogleCalendarProperties {

    /**
     * Google OAuth2 Client-ID für API-Authentifizierung.
     */
    private String clientId;

    /**
     * Google OAuth2 Client-Secret für sichere Authentifizierung.
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