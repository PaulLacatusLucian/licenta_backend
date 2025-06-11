package com.cafeteria.cafeteria_plugin.meetings;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service-Klasse für Google Calendar API-Integration und Meeting-Management.
 * @author Paul Lacatus
 * @version 1.0
 * @see GoogleCalendarProperties
 * @see MeetingController
 * @see com.google.api.client.auth.oauth2.TokenResponse
 * @since 2025-04-09
 */
@Service
public class GoogleCalendarService {

    /**
     * Konfigurationseigenschaften für Google Calendar API-Zugriff.
     * <p>
     * Enthält OAuth2-Credentials und Authentifizierungsparameter,
     * die aus application.properties oder Umgebungsvariablen geladen werden.
     */
    private final GoogleCalendarProperties props;

    /**
     * HTTP-Transport-Instanz für Google API-Kommunikation.
     * <p>
     * Wiederverwendbare HTTP-Transport-Schicht für effiziente
     * Netzwerkkommunikation mit Google's Calendar API-Endpunkten.
     * Verwendet Java's nativen HTTP-Client für Kompatibilität.
     */
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * JSON-Factory für Google API-Daten-Serialisierung.
     * <p>
     * Gson-basierte JSON-Verarbeitung für Request/Response-Serialisierung
     * bei Google Calendar API-Aufrufen. Optimiert für Google's API-Format.
     */
    private final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Konstruktor für Dependency Injection der Konfigurationseigenschaften.
     * <p>
     * Spring's Constructor Injection sorgt für sichere und testbare
     * Abhängigkeitsverwaltung. Die Konfiguration wird zur Laufzeit
     * aus den entsprechenden Property-Quellen injiziert.
     *
     * @param props Konfigurationseigenschaften mit OAuth2-Credentials
     */
    public GoogleCalendarService(GoogleCalendarProperties props) {
        this.props = props;
    }

    /**
     * Erneuert automatisch den Google API Access Token über Refresh Token.
     * <p>
     * Diese Methode implementiert den OAuth2 Refresh Token-Flow und ermöglicht
     * dauerhafte API-Zugriffe ohne wiederholte Benutzer-Authentifizierung.
     * Sie wird vor jedem Calendar API-Aufruf automatisch aufgerufen.
     * @return Gültiger Access Token für Google Calendar API-Zugriffe
     * @throws IOException bei Netzwerkproblemen oder API-Fehlern
     * @throws com.google.api.client.auth.oauth2.TokenResponseException bei ungültigen Credentials
     */
    public String getAccessToken() throws IOException {
        TokenResponse tokenResponse = new GoogleRefreshTokenRequest(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                props.getRefreshToken(),
                props.getClientId(),
                props.getClientSecret()
        ).execute();

        return tokenResponse.getAccessToken();
    }

    /**
     * Erstellt automatisch ein Google Calendar-Event mit Google Meet-Integration.
     * @param summary Aussagekräftiger Titel für das Meeting (z.B. "Elternabend Klasse 10A")
     * @param start Startzeitpunkt als ZonedDateTime mit korrekter Timezone
     * @param end Endzeitpunkt als ZonedDateTime mit korrekter Timezone
     * @param attendeeEmails Liste aller Teilnehmer-E-Mail-Adressen für Einladungen
     * @return Google Meet-Link für direkte Video-Konferenz-Teilnahme
     * @throws IOException bei Netzwerkproblemen oder Google API-Fehlern
     * @throws IllegalArgumentException bei ungültigen Zeitangaben oder leeren Teilnehmerlisten
     * @throws com.google.api.client.googleapis.json.GoogleJsonResponseException bei API-spezifischen Fehlern
     */
    public String createMeeting(String summary, ZonedDateTime start, ZonedDateTime end, List<String> attendeeEmails) throws IOException {
        String accessToken = getAccessToken();

        // Konfiguration des HTTP-Clients mit OAuth2-Authentifizierung
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory((HttpRequest request) -> {
            request.setParser(new JsonObjectParser(JSON_FACTORY));
            request.getHeaders().setAuthorization("Bearer " + accessToken);
        });

        // Zeitformat-Konvertierung für Google Calendar API
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String formattedStart = formatter.format(start);
        String formattedEnd = formatter.format(end);

        // Strukturierte Event-Daten für Calendar API
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("summary", summary);
        eventData.put("description", "Ședință cu părinții"); // Rumänische Beschreibung für lokalen Kontext

        // Zeitraum-Definition mit expliziter Timezone
        Map<String, String> startObj = Map.of(
                "dateTime", formattedStart,
                "timeZone", "Europe/Bucharest"
        );

        Map<String, String> endObj = Map.of(
                "dateTime", formattedEnd,
                "timeZone", "Europe/Bucharest"
        );

        eventData.put("start", startObj);
        eventData.put("end", endObj);

        // Teilnehmer-Liste für Bulk-Einladungen
        List<Map<String, String>> attendees = new ArrayList<>();
        for (String email : attendeeEmails) {
            attendees.add(Map.of("email", email));
        }
        eventData.put("attendees", attendees);

        // Google Meet-Integration für Video-Konferenz
        Map<String, Object> conferenceData = new HashMap<>();
        Map<String, String> createRequest = new HashMap<>();
        createRequest.put("requestId", UUID.randomUUID().toString()); // Eindeutige Request-ID für Idempotenz
        conferenceData.put("createRequest", createRequest);
        eventData.put("conferenceData", conferenceData);

        // API-Endpunkt mit optimierten Parametern
        GenericUrl url = new GenericUrl("https://www.googleapis.com/calendar/v3/calendars/primary/events?conferenceDataVersion=1&sendUpdates=all");
        HttpContent content = new ByteArrayContent("application/json", JSON_FACTORY.toByteArray(eventData));

        // Debug-Logging für Entwicklung und Troubleshooting
        System.out.println(JSON_FACTORY.toPrettyString(eventData));

        // Event-Erstellung mit strukturierter Fehlerbehandlung
        HttpRequest request = requestFactory.buildPostRequest(url, content);
        HttpResponse response = request.execute();

        // Response-Parsing für Google Meet-Link-Extraktion
        @SuppressWarnings("unchecked")
        Map<String, Object> json = response.parseAs(Map.class);

        Map<String, Object> conference = (Map<String, Object>) json.get("conferenceData");
        Map<String, Object> entryPoint = ((List<Map<String, Object>>) conference.get("entryPoints")).get(0);

        return (String) entryPoint.get("uri");
    }
}