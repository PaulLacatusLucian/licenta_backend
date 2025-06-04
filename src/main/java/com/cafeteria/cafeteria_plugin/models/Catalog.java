package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Modellklasse für den Klassenkatalog im Schulverwaltungssystem.
 * <p>
 * Diese Klasse repräsentiert einen digitalen Katalog für eine Schulklasse,
 * der alle Noten und Abwesenheiten der Schüler in strukturierter Form sammelt.
 * Der Katalog dient als zentraler Speicher für Bewertungen und ermöglicht
 * eine übersichtliche Darstellung der Leistungen aller Klassenmitglieder.
 * <p>
 * Funktionalitäten:
 * - Sammlung aller Noten und Abwesenheiten einer Klasse
 * - Strukturierte Organisation nach Schülern und Fächern
 * - Historische Verfolgung der Leistungsentwicklung
 * - Grundlage für Zeugniserstellung und Berichte
 * - Integration mit Klassensitzungen und Bewertungssystem
 * <p>
 * Beziehungen:
 * - One-to-One mit einer Schulklasse
 * - One-to-Many mit Katalogeinträgen (Noten/Abwesenheiten)
 * <p>
 * Verwendung:
 * - Automatische Erstellung bei Klassenerstellung
 * - Kontinuierliche Aktualisierung durch Lehrer
 * - Basis für Eltern- und Schülerberichte
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Class
 * @see CatalogEntry
 * @see EntryType
 * @since 2025-03-08
 */
@Data
@Entity
public class Catalog {

    /**
     * Eindeutige Identifikationsnummer des Katalogs.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Schulklasse, zu der dieser Katalog gehört.
     * <p>
     * Diese One-to-One-Beziehung stellt sicher, dass jede Klasse
     * genau einen Katalog hat. Der Katalog wird automatisch erstellt,
     * wenn eine neue Klasse angelegt wird, und enthält alle
     * Bewertungsinformationen für diese Klasse.
     */
    @OneToOne
    @JoinColumn(name = "class_id")
    private Class studentClass;

    /**
     * Liste aller Einträge in diesem Katalog.
     * <p>
     * Diese One-to-Many-Beziehung sammelt alle Noten und Abwesenheiten
     * der Schüler in dieser Klasse. Die Einträge werden automatisch
     * hinzugefügt, wenn Lehrer Noten vergeben oder Abwesenheiten registrieren.
     * <p>
     * Cascading-Verhalten:
     * - CascadeType.ALL: Alle Operationen werden auf Einträge übertragen
     * - Automatische Löschung aller Einträge bei Kataloglöschung
     * - Bidirektionale Beziehung für Datenintegrität
     */
    @OneToMany(mappedBy = "catalog", cascade = CascadeType.ALL)
    private List<CatalogEntry> entries = new ArrayList<>();
}