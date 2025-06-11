package com.cafeteria.cafeteria_plugin.meetings;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Data Transfer Object für Meeting-Erstellungsanfragen mit Google Calendar-Integration.
 * @author Paul Lacatus
 * @version 1.0
 * @see MeetingController
 * @see GoogleCalendarService
 * @see com.fasterxml.jackson.annotation.JsonFormat
 * @since 2025-04-09
 */
@Data
public class MeetingRequest {

    /**
     * Name der Schulklasse für organisatorische Zuordnung.
     */
    private String className;

    /**
     * Liste der E-Mail-Adressen aller Meeting-Teilnehmer.
     * <p>
     * Vollständige E-Mail-Liste für automatische Google Calendar-Einladungen
     * und Meeting-Benachrichtigungen. Unterstützt Bulk-Operationen für
     * große Eltern-Gruppen und flexible Teilnehmer-Zusammenstellungen.
     * <p>
     * Teilnehmer-Kategorien:
     * - Eltern-E-Mails für klassenbasierte Meetings
     * - Lehrer-E-Mails für Kollegiums-Konferenzen
     * - Administrative E-Mails für schulweite Events
     * - Externe Stakeholder für erweiterte Meetings
     */
    private List<String> parentEmails;

    /**
     * Startzeitpunkt des Meetings mit Timezone-Information.
     * <p>
     * Präzise Zeitangabe mit Timezone-Bewusstsein für internationale
     * Kompatibilität und korrekte Kalender-Darstellung. Unterstützt
     * optionale Angaben mit intelligenten Standard-Werten.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime startDateTime;

    /**
     * Endzeitpunkt des Meetings mit Timezone-Information.
     * <p>
     * Präzise End-Zeit-Definition für Meeting-Dauer-Management und
     * Kalender-Blockierung. Ermöglicht flexible Meeting-Längen und
     * automatische Dauer-Berechnung.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime endDateTime;
}