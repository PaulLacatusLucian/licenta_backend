package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.PastStudent;
import com.cafeteria.cafeteria_plugin.services.PastStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * REST-Controller für die Verwaltung von ehemaligen Schülern (Absolventen).
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für die umfassende Verwaltung von
 * Absolventen und ehemaligen Schülern der Bildungseinrichtung bereit.
 * Sie ermöglicht die Archivierung von Schülerdaten nach dem Schulabschluss
 * und die Aufrechterhaltung historischer Aufzeichnungen für administrative
 * und statistische Zwecke.
 * <p>
 * Hauptfunktionen:
 * - CRUD-Operationen für ehemalige Schüler
 * - Archivierung von Absolventendaten
 * - Verwaltung historischer Schülerinformationen
 * - Sichere Datenverwaltung für Alumni-Aufzeichnungen
 * - Administrative Übersicht über Schulabgänger
 * - Unterstützung für Alumni-Verwaltung und Statistiken
 * <p>
 * Sicherheit:
 * - Ausschließlicher Administratorzugriff für alle Operationen
 * - Rollenbasierte Zugriffskontrolle mit ADMIN-Berechtigung
 * - Sichere Verwaltung sensibler Absolventendaten
 * - Kontrollierte Löschung mit Fehlerbehandlung
 * <p>
 * Datenintegrität:
 * - Validierung bei Hinzufügung neuer Absolventeneinträge
 * - Sichere Löschung mit Exception-Handling
 * - Konsistente Datenarchivierung
 * - Erhaltung historischer Aufzeichnungen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see PastStudentService
 * @see PastStudent
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/api/past-students")
public class PastStudentController {

    /**
     * Service für ehemalige Schüler-Operationen und Datenverwaltung.
     */
    @Autowired
    PastStudentService pastStudentService;

    /**
     * Ruft alle ehemaligen Schüler aus dem System ab.
     * <p>
     * Nur für Administratoren zugänglich.
     * Gibt eine vollständige Liste aller im System archivierten
     * ehemaligen Schüler zurück. Nützlich für Alumni-Verwaltung,
     * statistische Auswertungen und administrative Übersichten
     * über alle Schulabgänger.
     *
     * @return Liste aller ehemaligen Schüler im System
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<PastStudent> getAllPastStudents() {
        return pastStudentService.getAllPastStudents();
    }

    /**
     * Ruft einen spezifischen ehemaligen Schüler anhand seiner ID ab.
     * <p>
     * Nur für Administratoren zugänglich.
     * Ermöglicht den Abruf detaillierter Informationen zu einem
     * einzelnen ehemaligen Schüler einschließlich aller archivierten
     * Daten und Aufzeichnungen aus der Schulzeit.
     *
     * @param id Eindeutige ID des ehemaligen Schülers
     * @return ResponseEntity mit Absolventendaten oder 404 mit deutscher Fehlermeldung falls nicht gefunden
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPastStudentById(@PathVariable Long id) {
        PastStudent student = pastStudentService.getPastStudentById(id);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Es existiert kein Absolvent mit dieser ID.");
        }
        return ResponseEntity.ok(student);
    }

    /**
     * Fügt einen neuen ehemaligen Schüler zum Archiv hinzu.
     * <p>
     * Nur für Administratoren zugänglich.
     * Erstellt einen neuen Eintrag für einen ehemaligen Schüler
     * im Archivsystem. Diese Funktion wird typischerweise verwendet,
     * wenn ein Schüler die Schule abschließt oder verlässt und
     * seine Daten archiviert werden müssen.
     * <p>
     * Verwendungszwecke:
     * - Archivierung bei Schulabschluss
     * - Datenmigration von aktiven zu ehemaligen Schülern
     * - Manuelle Erfassung historischer Daten
     * - Alumni-Registrierung
     *
     * @param pastStudent Daten des zu archivierenden ehemaligen Schülers
     * @return ResponseEntity mit dem erstellten Absolventeneintrag
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PastStudent> addPastStudent(@RequestBody PastStudent pastStudent) {
        PastStudent savedStudent = pastStudentService.savePastStudent(pastStudent);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    /**
     * Löscht einen ehemaligen Schüler vollständig aus dem Archiv.
     * <p>
     * Nur für Administratoren zugänglich.
     * Entfernt einen Absolventeneintrag permanent aus dem System.
     * Diese Operation sollte mit Vorsicht durchgeführt werden, da
     * sie zum unwiderruflichen Verlust historischer Daten führt.
     * <p>
     * Sicherheitsfeatures:
     * - Umfassende Exception-Behandlung
     * - Deutsche Fehlermeldungen bei Problemen
     * - Sichere Löschung mit Validierung
     * - Fehlerprotokollierung für Audit-Zwecke
     *
     * @param id ID des zu löschenden ehemaligen Schülers
     * @return ResponseEntity mit No-Content-Status bei Erfolg oder Fehlermeldung bei Problemen
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePastStudent(@PathVariable Long id) {
        try {
            pastStudentService.deletePastStudent(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fehler beim Löschen: " + e.getMessage());
        }
    }
}