package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Chef;
import com.cafeteria.cafeteria_plugin.services.ChefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller für alle köchebezogenen Operationen.
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für die Verwaltung von Köchen im
 * Cafeteria-System bereit. Köche sind verantwortlich für die Zubereitung
 * der Mahlzeiten und können ihre eigenen Profile einsehen und verwalten.
 * <p>
 * Hauptfunktionen:
 * - CRUD-Operationen für Köche (nur Admin)
 * - Self-Service für angemeldete Köche
 * - Verwaltung von Koch-Profilen und -Daten
 * - Rollenverwaltung für Cafeteria-Personal
 * <p>
 * Sicherheit:
 * - Rollenbasierte Zugriffskontrolle
 * - Administratoren haben vollständige CRUD-Rechte
 * - Köche können nur ihre eigenen Daten einsehen
 * - Fehlerbehandlung mit entsprechenden HTTP-Status-Codes
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see ChefService
 * @see Chef
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/chefs")
public class ChefController {

    /**
     * Service für Köche-Operationen.
     */
    @Autowired
    private ChefService chefService;

    /**
     * Ruft alle Köche im System ab.
     * <p>
     * Nur Administratoren können eine vollständige Liste aller Köche einsehen.
     * Dieser Endpunkt wird für administrative Übersichten und Verwaltungszwecke verwendet.
     *
     * @return ResponseEntity mit Liste aller Köche im System
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Chef>> getAllChefs() {
        return ResponseEntity.ok(chefService.getAllChefs());
    }

    /**
     * Ruft einen spezifischen Koch anhand seiner ID ab.
     * <p>
     * Zugänglich für Administratoren und den Koch selbst.
     * Administratoren können alle Koch-Profile einsehen, während
     * Köche nur ihre eigenen Daten abrufen können.
     *
     * @param id Eindeutige ID des Kochs
     * @return ResponseEntity mit den Koch-Daten oder 404 falls nicht gefunden
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getChefById(@PathVariable Long id) {
        return chefService.getChefById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Aktualisiert die Daten eines existierenden Kochs.
     * <p>
     * Nur Administratoren können Koch-Daten aktualisieren.
     * Ermöglicht die Änderung von Basisdaten wie Name, Benutzername und Passwort.
     * Fehlerhafte Eingaben werden mit entsprechenden Fehlermeldungen abgefangen.
     *
     * @param id          ID des zu aktualisierenden Kochs
     * @param chefDetails Koch-Objekt mit neuen Daten
     * @return ResponseEntity mit dem aktualisierten Koch oder Fehlermeldung
     *         - 200 OK: Aktualisierung erfolgreich
     *         - 400 Bad Request: Ungültige Eingabedaten
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateChef(@PathVariable Long id, @RequestBody Chef chefDetails) {
        try {
            Chef updatedChef = chefService.updateChef(id, chefDetails);
            return ResponseEntity.ok(updatedChef);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Löscht einen Koch vollständig aus dem System.
     * <p>
     * Nur Administratoren können Köche löschen.
     * Führt eine sichere Löschung durch und prüft dabei die Existenz des Kochs.
     * Bei Fehlern wird eine entsprechende Fehlermeldung zurückgegeben.
     *
     * @param id ID des zu löschenden Kochs
     * @return ResponseEntity mit Status-Code
     *         - 204 No Content: Löschung erfolgreich
     *         - 400 Bad Request: Koch existiert nicht oder andere Fehler
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChef(@PathVariable Long id) {
        try {
            chefService.deleteChef(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}