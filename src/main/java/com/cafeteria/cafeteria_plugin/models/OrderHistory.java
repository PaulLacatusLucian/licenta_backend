package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Modellklasse für die Bestellhistorie der Schul-Cafeteria.
 * @author Paul Lacatus
 * @version 1.0
 * @see MenuItem
 * @see Parent
 * @see Student
 * @since 2024-11-28
 */
@Data
@Entity
public class OrderHistory {

    /**
     * Eindeutige Identifikationsnummer der Bestellung.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name des bestellten Menüpunkts zum Zeitpunkt der Bestellung.
     * <p>
     * Wird separat gespeichert, um historische Genauigkeit zu gewährleisten,
     * auch wenn der ursprüngliche Menüpunkt später geändert oder gelöscht wird.
     * Ermöglicht korrekte Rechnungsstellung und Nachverfolgung.
     */
    private String menuItemName;

    /**
     * Gesamtpreis der Bestellung zum Zeitpunkt der Aufgabe.
     * <p>
     * Berechnet als Einzelpreis × Menge zum Bestellzeitpunkt.
     * Wird separat gespeichert, um Preisänderungen in der Zukunft
     * nicht rückwirkend auf alte Bestellungen anzuwenden.
     */
    private Double price;

    /**
     * Anzahl der bestellten Einheiten.
     * <p>
     * Gibt an, wie viele Portionen des Menüpunkts bestellt wurden.
     * Wird für Lagerbestandsreduktion und Preisberechnung verwendet.
     */
    private Integer quantity;

    /**
     * Zeitpunkt der Bestellaufgabe.
     * <p>
     * Automatisch gesetzt zum Zeitpunkt der Bestellerstellung.
     * Wird für Berichte, Rechnungsstellung und zeitbasierte Analysen verwendet.
     */
    private LocalDateTime orderTime;

    /**
     * Elternteil, der die Bestellung aufgegeben und bezahlt hat.
     * <p>
     * Diese Many-to-One-Beziehung identifiziert den Zahler der Bestellung.
     * Ermöglicht die Erstellung elternspezifischer Rechnungen und
     * die Verfolgung von Zahlungshistorien.
     */
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    /**
     * Schüler, für den die Bestellung bestimmt ist.
     * <p>
     * Diese Many-to-One-Beziehung identifiziert den Empfänger der Bestellung.
     * Ein Elternteil kann für mehrere Kinder bestellen, daher ist diese
     * separate Zuordnung wichtig für korrekte Lieferung und Verfolgung.
     */
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}