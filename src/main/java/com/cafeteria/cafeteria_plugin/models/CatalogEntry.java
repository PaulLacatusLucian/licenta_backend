package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * Modellklasse für einzelne Einträge im Klassenkatalog.
 * @author Paul Lacatus
 * @version 1.0
 * @see Catalog
 * @see EntryType
 * @see Student
 * @since 2025-03-08
 */
@Entity
@Data
public class CatalogEntry {

    /**
     * Eindeutige Identifikationsnummer des Katalogeintrags.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Katalog, zu dem dieser Eintrag gehört.
     *
     * Diese Many-to-One-Beziehung verknüpft den Eintrag mit dem
     * entsprechenden Klassenkatalog. JsonBackReference verhindert
     * zirkuläre Referenzen bei der JSON-Serialisierung.
     */
    @ManyToOne
    @JoinColumn(name = "catalog_id")
    @JsonBackReference
    private Catalog catalog;

    /**
     * Schüler, auf den sich dieser Eintrag bezieht.
     *
     * Diese Many-to-One-Beziehung identifiziert eindeutig den Schüler,
     * für den die Note oder Abwesenheit registriert wurde.
     */
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    /**
     * Typ des Katalogeintrags.
     *
     * Bestimmt, ob es sich um eine Note (GRADE) oder eine
     * Abwesenheit (ABSENCE) handelt. Wird als Enumeration
     * in der Datenbank gespeichert.
     */
    @Enumerated(EnumType.STRING)
    private EntryType type;

    /**
     * Unterrichtsfach, für das der Eintrag gilt.
     *
     * Ermöglicht die Zuordnung von Noten und Abwesenheiten
     * zu spezifischen Schulfächern für detaillierte Analysen.
     *
     * Beispiele: "Mathematik", "Deutsch", "Geschichte", "Sport"
     */
    private String subject;

    /**
     * Numerischer Wert der Note (nur für GRADE-Einträge).
     *
     * Enthält den Bewertungswert bei Noten-Einträgen.
     * Für Abwesenheits-Einträge bleibt dieses Feld null.
     *
     * Wertebereich: Typischerweise 1.0 bis 10.0 im rumänischen System
     */
    private Double gradeValue;

    /**
     * Begründungsstatus für Abwesenheiten (nur für ABSENCE-Einträge).
     *
     * Gibt an, ob eine Abwesenheit berechtigt/entschuldigt ist:
     * - true: Entschuldigte Abwesenheit (z.B. Krankheit, Arzttermin)
     * - false: Unentschuldigte Abwesenheit
     * - null: Noch nicht bewertet oder nicht anwendbar
     *
     * Für Noten-Einträge bleibt dieses Feld null.
     */
    private Boolean justified;

    /**
     * Zeitstempel der Eintragserstellung.
     *
     * Wird automatisch auf die aktuelle Zeit gesetzt, wenn der
     * Eintrag erstellt wird. Ermöglicht chronologische Sortierung
     * und historische Verfolgung der Einträge.
     */
    private LocalDateTime date = LocalDateTime.now();

    private Long absenceId;
}