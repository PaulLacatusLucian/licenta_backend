package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object für Cafeteria-Bestellhistorie.
 * <p>
 * Diese Klasse kapselt alle relevanten Informationen über eine Cafeteria-Bestellung
 * für die Übertragung zwischen verschiedenen Anwendungsschichten. Sie stellt eine
 * vollständige, serialisierbare Darstellung der OrderHistory-Entität dar und
 * integriert Benutzer-Informationen für umfassende Bestellverfolgung.
 * <p>
 * Das DTO enthält:
 * - Grundlegende Bestellinformationen
 * - Menüartikel-Details mit Preisen
 * - Besteller- und Empfänger-Informationen
 * - Zeitstempel für Bestellverfolgung
 * - Vollständige Transaktionsdokumentation
 * <p>
 * Verwendungsszenarien:
 * - REST API Responses für Bestellhistorie-Abfragen
 * - Eltern-Portale für Ausgaben-Übersichten
 * - Schüler-Dashboards für Essenshistorie
 * - Abrechnungen und Rechnungsstellung
 * - Cafeteria-Management und Statistiken
 * <p>
 * Technische Eigenschaften:
 * - Vollständige Aggregation verwandter Benutzer-Daten
 * - JSON-serialisierbar für REST APIs
 * - Optimiert für Finanz- und Statistik-Berichte
 * - Vermeidung von Entity-Beziehungen für Performance
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see com.cafeteria.cafeteria_plugin.models.OrderHistory
 * @see StudentDTO
 * @see ParentDTO
 * @see com.cafeteria.cafeteria_plugin.models.MenuItem
 * @since 2025-01-01
 */
@Data
public class OrderHistoryDTO {

    /**
     * Eindeutige Identifikation der Bestellung.
     * <p>
     * Primärschlüssel der OrderHistory-Entität:
     * - Referenz für Bestellverfolgung
     * - API-Endpunkt-Parameter für spezifische Bestellungen
     * - Basis für Stornierungen und Änderungen
     * - Eindeutige Transaktions-ID
     */
    private Long id;

    /**
     * Name des bestellten Menüartikels.
     * <p>
     * Bezeichnung des Essens oder Getränks:
     * - Benutzerfreundliche Produktidentifikation
     * - Basis für Bestellstatistiken
     * - Wichtig für Allergiker-Informationen
     * - Referenz für Menü-Verwaltung
     * <p>
     * Hinweis: Gespeichert als String für historische Konsistenz,
     * auch wenn Menüartikel später geändert oder entfernt werden.
     */
    private String menuItemName;

    /**
     * Gesamtpreis der Bestellung.
     * <p>
     * Endpreis inklusive aller Mengen und Anpassungen:
     * - Basis für Abrechnungen und Rechnungen
     * - Wichtig für Ausgaben-Tracking
     * - Finanzielle Statistiken und Berichte
     * - Eltern-Budget-Überwachung
     * <p>
     * Berechnung: Einzelpreis × Menge zum Zeitpunkt der Bestellung
     */
    private Double price;

    /**
     * Anzahl der bestellten Artikel.
     * <p>
     * Stückzahl des Menüartikels:
     * - Basis für Preis-Berechnung
     * - Wichtig für Inventar-Management
     * - Verbrauchsstatistiken
     * - Portionsgrößen-Analysen
     */
    private int quantity;

    /**
     * Zeitstempel der Bestellung.
     * <p>
     * Exakte Zeit der Bestellaufgabe:
     * - Chronologische Sortierung
     * - Basis für zeitbasierte Analysen
     * - Wichtig für Lieferzeiten-Berechnung
     * - Abrechnungsperioden-Zuordnung
     * - Essenszeiten-Statistiken
     */
    private LocalDateTime orderTime;

    /**
     * Vollständige Schüler-Informationen als verschachtelte DTO.
     * <p>
     * Enthält alle relevanten Empfänger-Daten:
     * - Name, Klasse, Kontaktinformationen
     * - Allergiker-Informationen für Sicherheit
     * - Optimiert für Lieferung und Abholung
     * - Vermeidet zusätzliche API-Calls
     * <p>
     * Der Schüler ist der tatsächliche Empfänger der Bestellung,
     * auch wenn ein Elternteil bestellt und bezahlt hat.
     */
    private StudentDTO student;

    /**
     * Vollständige Eltern-Informationen als verschachtelte DTO.
     * <p>
     * Enthält alle relevanten Besteller-Daten:
     * - Kontaktinformationen für Rechnungsstellung
     * - Zahlungsinformationen und Präferenzen
     * - Optimiert für Finanz-Management
     * - Basis für Eltern-Kommunikation
     * <p>
     * Der Elternteil ist der Besteller und Zahlungspflichtige,
     * während der Schüler der Empfänger ist.
     */
    private ParentDTO parent;
}