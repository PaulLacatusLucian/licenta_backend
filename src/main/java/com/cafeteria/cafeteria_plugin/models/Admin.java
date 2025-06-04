package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Modellklasse für Systemadministratoren im Schulverwaltungssystem.
 * <p>
 * Diese Klasse erweitert die abstrakte User-Klasse und repräsentiert einen
 * Administrator mit vollständigen Systemberechtigungen. Administratoren
 * haben die höchste Berechtigung im System und können alle Funktionen
 * verwalten und konfigurieren.
 * <p>
 * Administratorrechte umfassen:
 * - Vollständige Benutzerverwaltung (Erstellen, Bearbeiten, Löschen aller Benutzertypen)
 * - Klassenverwaltung und Schulstrukturkonfiguration
 * - Systemkonfiguration und -wartung
 * - Cafeteria-Management und -überwachung
 * - Jahresübergang und Absolventenverwaltung
 * - Alle Berichte und Analytik
 * <p>
 * Sicherheitsmerkmale:
 * - Automatische Benutzertyp-Zuweisung bei Erstellung
 * - Höchste Berechtigung in der Rollenhierarchie
 * - Zugriff auf alle geschützten Systemfunktionen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see User
 * @see UserType#ADMIN
 * @since 2025-03-12
 */
@Entity
@Getter
@Setter
public class Admin extends User {

    /**
     * Standard-Konstruktor für Admin-Instanzen.
     * <p>
     * Dieser Konstruktor ruft den Basis-Konstruktor auf und setzt
     * automatisch den Benutzertyp auf ADMIN. Dies stellt sicher,
     * dass jede Admin-Instanz korrekt als Administrator identifiziert wird.
     */
    public Admin() {
        super();
        this.setUserType(UserType.ADMIN);
    }
}