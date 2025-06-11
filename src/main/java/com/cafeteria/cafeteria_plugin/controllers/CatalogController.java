package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.CatalogEntry;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.CatalogService;
import com.cafeteria.cafeteria_plugin.services.ClassService;
import com.cafeteria.cafeteria_plugin.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-Controller für alle katalogbezogenen Operationen.
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für den Zugriff auf Schulkataloge bereit,
 * die alle Noten und Abwesenheiten von Schülern enthalten. Der Katalog dient
 * als zentrale Übersicht über die schulischen Leistungen und Anwesenheit.
 * <p>
 * Hauptfunktionen:
 * - Abruf von Katalogeinträgen für gesamte Klassen
 * - Abruf von Katalogeinträgen für einzelne Schüler
 * - Bereitstellung von Noten- und Abwesenheitsdaten
 * - Unterstützung verschiedener Benutzerrollen (Lehrer, Admin, Eltern)
 * <p>
 * Sicherheit:
 * - Rollenbasierte Zugriffskontrolle
 * - Kontextabhängige Berechtigung je nach Endpunkt
 * - JWT-Token-Validierung (vorbereitet für erweiterte Funktionalität)
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see CatalogService
 * @see CatalogEntry
 * @since 2025-05-08
 */
@RestController
@RequestMapping("/catalog")
public class CatalogController {

    /**
     * Service für Katalogoperationen.
     */
    @Autowired
    private CatalogService catalogService;

    /**
     * Service für Klassenoperationen.
     */
    @Autowired
    private ClassService classService;

    /**
     * Service für Lehreroperationen.
     */
    @Autowired
    private TeacherService teacherService;

    /**
     * Utility für JWT-Token-Verarbeitung.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Ruft alle Katalogeinträge für eine bestimmte Klasse ab.
     * <p>
     * Nur Lehrer und Administratoren können Klassenkataloge einsehen.
     * Gibt eine vollständige Übersicht aller Noten und Abwesenheiten
     * aller Schüler der angegebenen Klasse zurück.
     *
     * @param classId ID der Klasse, deren Katalog abgerufen werden soll
     * @return ResponseEntity mit Liste aller Katalogeinträge der Klasse
     */
    @GetMapping("/class/{classId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<List<CatalogEntry>> getCatalogForClass(@PathVariable Long classId) {
        List<CatalogEntry> entries = catalogService.getCatalogEntriesForClass(classId);
        return ResponseEntity.ok(entries);
    }

    /**
     * Ruft alle Katalogeinträge für einen bestimmten Schüler ab.
     * <p>
     * Zugänglich für Lehrer, Administratoren und Eltern.
     * Gibt eine vollständige Übersicht aller Noten und Abwesenheiten
     * des angegebenen Schülers zurück.
     * <p>
     * Anwendungsfälle:
     * - Lehrer: Einsicht in Schülerleistungen
     * - Administratoren: Vollständige Übersicht
     * - Eltern: Einsicht in die Leistungen ihrer Kinder
     *
     * @param studentId ID des Schülers, dessen Katalog abgerufen werden soll
     * @return ResponseEntity mit Liste aller Katalogeinträge des Schülers
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('PARENT')")
    public ResponseEntity<List<CatalogEntry>> getCatalogForStudent(@PathVariable Long studentId) {
        List<CatalogEntry> entries = catalogService.getStudentEntries(studentId);
        return ResponseEntity.ok(entries);
    }
}