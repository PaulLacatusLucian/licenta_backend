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