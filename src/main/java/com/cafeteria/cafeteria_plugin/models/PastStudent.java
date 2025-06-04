package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

/**
 * Modellklasse für Absolventen und ehemalige Schüler des Schulsystems.
 * <p>
 * Diese Klasse speichert grundlegende Informationen über Schüler, die
 * das Schulsystem verlassen haben, entweder durch Abschluss oder andere
 * Umstände. Sie dient als Archiv für historische Daten und Alumni-Verwaltung.
 * <p>
 * Entstehung von PastStudent-Einträgen:
 * - Automatisch beim Jahresübergang für Schüler der 12. Klasse
 * - Manuell bei vorzeitigem Schulwechsel oder -abbruch
 * - Bei Systemumstellungen oder Datenmigration
 * <p>
 * Unterschied zu aktiven Schülern:
 * - Keine Benutzerkonten oder Anmeldedaten
 * - Reduzierte Datenmenge (nur Name und Profil)
 * - Keine aktive Teilnahme am Schulsystem
 * - Primär für Archivierung und statistische Zwecke
 * <p>
 * Verwendungszwecke:
 * - Alumni-Verwaltung und -kontakt
 * - Statistische Auswertungen über Abschlussraten
 * - Historische Nachverfolgung von Bildungswegen
 * - Compliance mit Datenschutzbestimmungen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Student
 * @since 2025-01-15
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PastStudent {

    /**
     * Eindeutige Identifikationsnummer des ehemaligen Schülers.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Vollständiger Name des ehemaligen Schülers.
     * <p>
     * Wird aus den ursprünglichen Schülerdaten übernommen, wenn
     * der Schüler in die Absolventenliste überführt wird.
     * Ermöglicht spätere Identifikation für Alumni-Aktivitäten.
     */
    private String name;

    /**
     * Bildungsprofil oder Spezialisierung des Absolventen.
     * <p>
     * Enthält Informationen über die Spezialisierung oder den
     * Bildungsweg, den der Schüler eingeschlagen hat:
     * <p>
     * Beispiele:
     * - "Mathematik-Informatik" (bei Abschluss einer entsprechenden Klasse)
     * - "Filologie" (bei sprachwissenschaftlicher Ausrichtung)
     * - "Bio-Chemie" (bei naturwissenschaftlicher Spezialisierung)
     * - "Grundschulabschluss" (bei frühem Schulwechsel)
     * <p>
     * Dieses Feld wird automatisch aus der Klassenspezialisierung
     * übernommen oder manuell gesetzt bei besonderen Umständen.
     */
    private String profile;
}