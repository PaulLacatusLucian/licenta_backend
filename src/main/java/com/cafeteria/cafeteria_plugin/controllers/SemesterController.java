package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Semester;
import com.cafeteria.cafeteria_plugin.services.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST-Controller für die Verwaltung von Schulhalbjahren und Semestern.
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für die Verwaltung des aktuellen
 * Schulhalbjahres bereit und ermöglicht die Abfrage und Aktualisierung
 * des Semesterstatus. Sie ist essentiell für die zeitliche Organisation
 * des Schuljahres und die korrekte Zuordnung von akademischen Aktivitäten
 * zu den entsprechenden Semesterperioden.
 * <p>
 * Hauptfunktionen:
 * - Abruf des aktuellen Schulhalbjahres
 * - Übergang zum nächsten Semester
 * - Zentrale Semesterverwaltung für das gesamte Schulsystem
 * - Unterstützung für semesterbasierte Berichterstattung
 * - Automatische Semesterfortschreibung
 * <p>
 * Anwendungsbereich:
 * - Akademische Jahresplanung
 * - Semesterbasierte Notenverteilung
 * - Zeitliche Zuordnung von Unterrichtsstunden
 * - Administrative Semesterübergänge
 * - Systemweite Semestersynchronisation
 * <p>
 * Sicherheit:
 * - Öffentlicher Zugriff für Semesterabfrage
 * - Kontrollierte Semesteraktualisierung
 * - Systemkonsistenz bei Semesterübergängen
 * - Automatische Validierung bei Semesterwechsel
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see SemesterService
 * @see Semester
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/semesters")
public class SemesterController {

    /**
     * Service für Semester-Operationen und Halbjahresverwaltung.
     */
    @Autowired
    SemesterService semesterService;

    /**
     * Ruft das aktuell aktive Schulhalbjahr ab.
     * <p>
     * Öffentlich zugängliche Methode, die das derzeit gültige Semester
     * im Schulsystem zurückgibt. Diese Information ist fundamental für
     * alle zeitabhängigen Operationen wie Noteneintragung, Stundenplanung
     * und akademische Berichterstattung.
     * <p>
     * Verwendungszwecke:
     * - Anzeige des aktuellen Semesters in Benutzeroberflächen
     * - Zeitliche Zuordnung neuer akademischer Einträge
     * - Filterung von Daten nach Semesterperioden
     * - Validierung semesterabhängiger Operationen
     * - Frontend-Synchronisation mit Schuljahr-Status
     *
     * @return ResponseEntity mit dem aktuellen Semester-Objekt
     */
    @GetMapping("/current")
    public ResponseEntity<Semester> getCurrentSemester() {
        Semester currentSemester = semesterService.getCurrentSemester();
        return ResponseEntity.ok(currentSemester);
    }

    /**
     * Führt den Übergang zum nächsten Schulhalbjahr durch.
     * <p>
     * Administrative Funktion, die das Schulsystem vom aktuellen
     * zum nächsten Semester weiterschaltet. Diese Operation ist
     * kritisch für die korrekte zeitliche Organisation des Schuljahres
     * und sollte typischerweise zu Beginn eines neuen Halbjahres
     * durchgeführt werden.
     * <p>
     * Funktionalität:
     * - Automatische Inkrementierung des Semesterzählers
     * - Systemweite Aktualisierung des Semesterstatus
     * - Vorbereitung für neue semesterbasierte Daten
     * - Archivierung des vorherigen Semesters
     * - Initialisierung neuer Semesterstrukturen
     * <p>
     * Auswirkungen:
     * - Alle neuen Noten werden dem neuen Semester zugeordnet
     * - Neue Unterrichtsstunden gehören zum aktualisierten Semester
     * - Berichte und Statistiken werden entsprechend aktualisiert
     * - Historische Daten bleiben dem vorherigen Semester zugeordnet
     *
     * @return ResponseEntity mit dem neu aktualisierten Semester-Objekt
     */
    @PostMapping("/next")
    public ResponseEntity<Semester> incrementSemester() {
        Semester updatedSemester = semesterService.incrementSemester();
        return ResponseEntity.ok(updatedSemester);
    }
}