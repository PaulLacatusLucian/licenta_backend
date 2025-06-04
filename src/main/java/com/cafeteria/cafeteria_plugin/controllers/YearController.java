package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.services.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-Controller für Schuljahr-Verwaltungsoperationen.
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für administrative Operationen
 * im Zusammenhang mit dem Schuljahr bereit, insbesondere für den
 * Übergang zwischen Schuljahren.
 * <p>
 * Hauptfunktionen:
 * - Durchführung der jährlichen Schülerversetzung
 * - Verarbeitung von Abschlussklassen (Graduierung)
 * - Erstellung neuer Klassen für versetzte Schüler
 * - Archivierung von Absolventen
 * <p>
 * Sicherheit:
 * - Nur für Administratoren zugänglich
 * - Alle Endpunkte erfordern ADMIN-Rolle
 * - Geschützt durch Spring Security PreAuthorize
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see StudentService
 * @since 2025-01-01
 */
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/year")
public class YearController {

    /**
     * Service für Schüleroperationen und Jahresversetzung.
     */
    private final StudentService studentService;

    /**
     * Konstruktor für YearController.
     *
     * @param studentService Service für Schülerverwaltung und Jahresoperationen
     */
    public YearController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Startet ein neues Schuljahr und führt die Schülerversetzung durch.
     * <p>
     * Dieser Endpunkt führt folgende Operationen aus:
     * - Versetzt alle Schüler in die nächsthöhere Klasse
     * - Graduiert Schüler der 12. Klasse (Abschlussklassen)
     * - Erstellt automatisch neue Klassen falls erforderlich
     * - Archiviert Absolventen in der ehemaligen Schülerliste
     * <p>
     * Nur Administratoren können diese kritische Operation durchführen.
     * Die Operation ist transaktional und wird vollständig oder gar nicht ausgeführt.
     *
     * @return ResponseEntity mit Erfolgsmeldung oder Fehlerbeschreibung
     *         - 200 OK: Jahresversetzung erfolgreich durchgeführt
     *         - 500 Internal Server Error: Fehler bei der Durchführung
     */
    @PostMapping("/start-new-year")
    public ResponseEntity<String> startNewYear() {
        try {
            studentService.advanceYear();
            return ResponseEntity.ok("Das Schuljahr wurde erfolgreich vorangebracht!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler beim Voranschreiten des Schuljahres: " + e.getMessage());
        }
    }
}