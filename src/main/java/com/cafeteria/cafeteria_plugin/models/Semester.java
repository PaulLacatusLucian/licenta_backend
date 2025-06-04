package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Modellklasse für die Semesterverwaltung im Schulverwaltungssystem.
 * <p>
 * Diese Klasse verwaltet den aktuellen Semesterstand des Schulsystems
 * und ermöglicht die systematische Nachverfolgung der Schuljahreszyklen.
 * Sie dient als zentrale Referenz für semesterbezogene Operationen.
 * <p>
 * Semestersystem:
 * - Semester 1: Erstes Halbjahr (September - Januar)
 * - Semester 2: Zweites Halbjahr (Februar - Juni)
 * - Automatische Verwaltung durch administrative Funktionen
 * - Grundlage für Bewertungsperioden und Zeugnisse
 * <p>
 * Verwendung:
 * - Zentrale Kontrolle über Schuljahreszyklen
 * - Basis für Bewertungsperioden und Berichte
 * - Integration mit Notenverwaltung und Zeugniserstellung
 * - Automatisierung von Jahresübergängen
 * <p>
 * Systemdesign:
 * - Singleton-Pattern: Nur ein Semester-Datensatz im System
 * - Administrative Kontrolle über Semesterübergänge
 * - Historische Nachverfolgung durch Versionierung möglich
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Grade
 * @see Student
 * @since 2025-01-12
 */
@Data
@Entity
public class Semester {

    /**
     * Eindeutige Identifikationsnummer des Semestereintrags.
     * <p>
     * In der aktuellen Implementierung wird ID 1 als Singleton verwendet,
     * um den aktuellen Semesterstand zu speichern.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Aktueller Semesterstand im Schulsystem.
     * <p>
     * Repräsentiert das derzeit aktive Semester:
     * - 1: Erstes Semester (Herbst/Winter)
     * - 2: Zweites Semester (Frühling/Sommer)
     * <p>
     * Geschäftslogik:
     * - Startwert ist 1 bei Systeminitialisierung
     * - Wird durch administrative Aktionen aktualisiert
     * - Basis für semesterspezifische Berichte und Bewertungen
     * - Kann für zukünftige Erweiterungen auf mehr als 2 Semester erweitert werden
     */
    @Column(nullable = false)
    private Integer currentSemester = 1;
}