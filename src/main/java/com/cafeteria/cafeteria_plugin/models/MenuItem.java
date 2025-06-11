package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Modellklasse für Menüpunkte in der Schul-Cafeteria.
 * @author Paul Lacatus
 * @version 1.0
 * @see OrderHistory
 * @since 2024-11-28
 */
@Data
@Entity
public class MenuItem {

    /**
     * Eindeutige Identifikationsnummer des Menüpunkts.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name des Menüpunkts.
     * <p>
     * Sollte kurz und aussagekräftig sein.
     * Beispiele: "Schnitzel mit Pommes", "Vegetarische Pizza", "Apfelsaft"
     */
    private String name;

    /**
     * Detaillierte Beschreibung des Menüpunkts.
     * <p>
     * Kann Informationen enthalten über:
     * - Zutaten und Zubereitung
     * - Portionsgröße
     * - Besondere Eigenschaften (vegetarisch, vegan, etc.)
     * - Nährwertinformationen
     */
    private String description;

    /**
     * Preis des Menüpunkts in der lokalen Währung.
     * <p>
     * Wird für Bestellberechnungen und Rechnungsstellung verwendet.
     * Sollte inklusive aller Steuern und Gebühren sein.
     */
    private Double price;

    /**
     * URL oder Pfad zum Bild des Menüpunkts.
     * <p>
     * Optional, aber empfohlen für bessere Benutzererfahrung.
     * Wird in der Bestelloberfläche angezeigt.
     */
    private String imageUrl;

    /**
     * Verfügbare Menge/Anzahl des Menüpunkts.
     * <p>
     * Wird für Lagerverwaltung verwendet:
     * - Verhindert Überbestellungen
     * - Ermöglicht automatische Benachrichtigungen bei niedrigem Bestand
     * - Wird bei jeder Bestellung automatisch reduziert
     */
    private Integer quantity;

    /**
     * Liste der Allergene, die in diesem Menüpunkt enthalten sind.
     * <p>
     * Wichtig für Schüler mit Allergien oder Unverträglichkeiten.
     * Beispiele: "Gluten", "Milch", "Nüsse", "Eier", "Soja"
     * <p>
     * Wird als separate Tabelle gespeichert für flexible Abfragen.
     */
    @ElementCollection
    private List<String> allergens;
}