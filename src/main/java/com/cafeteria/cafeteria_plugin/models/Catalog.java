package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Modellklasse für den Klassenkatalog im Schulverwaltungssystem.
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