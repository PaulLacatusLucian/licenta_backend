package com.cafeteria.cafeteria_plugin.meetings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * REST Controller für Meeting-Management und Google Calendar-Integration.
 * <p>
 * Diese Klasse stellt RESTful API-Endpunkte für die automatische Erstellung
 * und Verwaltung von Schul-Meetings bereit. Sie ermöglicht autorisierten
 * Benutzern (Lehrern und Administratoren) die nahtlose Integration mit
 * Google Calendar und die automatische Generierung von Video-Konferenz-Links.
 * <p>
 * Hauptfunktionalitäten:
 * - RESTful Meeting-Erstellung mit Google Calendar-Integration
 * - Automatische Google Meet-Link-Generierung für Video-Konferenzen
 * - Bulk-Einladungen für Eltern-Gruppen und Klassenlisten
 * - Rollenbasierte Zugriffskontrolle für Meeting-Management
 * - Flexible Zeitplanung mit Standard-Werten für schnelle Event-Erstellung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see GoogleCalendarService
 * @see MeetingRequest
 * @see org.springframework.security.access.prepost.PreAuthorize
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/meetings")
public class MeetingController {

    /**
     * Service für Google Calendar API-Integration und Meeting-Management.
     * <p>
     * Wird für die tatsächliche Meeting-Erstellung und Google Calendar-
     * Kommunikation verwendet. Integration erfolgt über Spring's Dependency Injection.
     */
    @Autowired
    GoogleCalendarService calendarService;

    /**
     * Erstellt ein neues Meeting mit Google Calendar-Integration und automatischen Einladungen.
     * <p>
     * Dieser API-Endpunkt ermöglicht autorisierten Benutzern die Erstellung von
     * Schul-Meetings mit automatischer Google Calendar-Integration und Google Meet-
     * Video-Konferenz-Generierung. Er ist optimiert für Bulk-Einladungen und
     * flexible Zeitplanung.
     *
     * @param request MeetingRequest mit Meeting-Details und Teilnehmer-Listen
     * @param token JWT Authorization-Token für Benutzer-Authentifizierung und Autorisierung
     * @return Google Meet-Link als String für direkte Video-Konferenz-Teilnahme
     * @throws IOException bei Google Calendar API-Problemen oder Netzwerkfehlern
     * @throws IllegalArgumentException bei ungültigen Request-Daten oder Zeitangaben
     * @throws org.springframework.security.access.AccessDeniedException bei unzureichender Berechtigung
     *
     * @apiNote
     * Request Body Beispiel:
     * <pre>
     * {
     *   "className": "Klasse 10A",
     *   "parentEmails": ["eltern1@example.com", "eltern2@example.com"],
     *   "startDateTime": "2025-01-15T19:00:00+02:00",
     *   "endDateTime": "2025-01-15T20:30:00+02:00"
     * }
     * </pre>
     *
     * Response Beispiel:
     * <pre>
     * "https://meet.google.com/abc-defg-hij"
     * </pre>
     */
    @PostMapping("/start")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public String startMeeting(@RequestBody MeetingRequest request, @RequestHeader("Authorization") String token) throws IOException {

        // Intelligente Standard-Zeitplanung für schnelle Meeting-Erstellung
        ZonedDateTime start = request.getStartDateTime() != null ? request.getStartDateTime() : ZonedDateTime.now().plusMinutes(5);
        ZonedDateTime end = request.getEndDateTime() != null ? request.getEndDateTime() : start.plusMinutes(30);

        // Google Calendar-Integration mit automatischer Meet-Link-Generierung
        return calendarService.createMeeting(
                "Ședință cu părinții - " + request.getClassName(), // Lokalisierter Meeting-Titel
                start,
                end,
                request.getParentEmails()
        );
    }
}