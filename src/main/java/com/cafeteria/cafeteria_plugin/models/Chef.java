package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.*;

/**
 * Modellklasse für Küchenpersonal im Schulverwaltungssystem.
 * <p>
 * Diese Klasse erweitert die abstrakte User-Klasse und repräsentiert einen
 * Koch oder Küchenmitarbeiter, der für die Verwaltung der Schul-Cafeteria
 * verantwortlich ist. Köche haben spezialisierte Berechtigungen für das
 * Cafeteria-Management.
 * <p>
 * Koch-Berechtigungen umfassen:
 * - Menü-Management (Hinzufügen, Bearbeiten, Löschen von Speisen)
 * - Lagerbestandsverwaltung und Bestellüberwachung
 * - Preisgestaltung und Produktinformationen
 * - Allergenkennzeichnung und Diätinformationen
 * - Verkaufsstatistiken und Popularitätsanalysen
 * - Bestellhistorie und Kundenmanagement
 * <p>
 * Arbeitsbereich:
 * - Primär auf Cafeteria-Funktionen beschränkt
 * - Keine Zugriff auf Schülernoten oder Anwesenheitsdaten
 * - Begrenzte Einsicht in Benutzerverwaltung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see User
 * @see UserType#CHEF
 * @see MenuItem
 * @see OrderHistory
 * @since 2025-01-31
 */
@Entity
@Table(name = "chefs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Chef extends User {

    /**
     * Vollständiger Name des Kochs.
     * <p>
     * Wird für Anzeige in der Benutzeroberfläche und zur Identifikation
     * in der Küchenverwaltung verwendet. Der Name wird auch für die
     * automatische Generierung von Benutzername und Passwort genutzt.
     */
    private String name;
}