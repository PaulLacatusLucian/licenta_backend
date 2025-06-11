package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Modellklasse für Systemadministratoren im Schulverwaltungssystem.
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