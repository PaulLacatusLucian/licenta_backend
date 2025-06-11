package com.cafeteria.cafeteria_plugin.models;

import com.cafeteria.cafeteria_plugin.email.PasswordResetToken;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Abstrakte Basisklasse für alle Benutzertypen im System.
 * @author Paul Lacatus
 * @version 1.0
 * @see Student
 * @see Parent
 * @see Teacher
 * @see Chef
 * @see Admin
 * @since 2024-11-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User {

    /**
     * Eindeutige Identifikationsnummer des Benutzers.
     * Wird automatisch von der Datenbank generiert.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Eindeutiger Benutzername für die Anmeldung.
     * Muss systemweit einzigartig sein und darf nicht null sein.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Verschlüsseltes Passwort des Benutzers.
     * Wird mit BCrypt verschlüsselt gespeichert.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Email-Adresse des Benutzers.
     * Muss systemweit einzigartig sein und wird für Benachrichtigungen verwendet.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Typ des Benutzers, der die Rolle und Berechtigungen bestimmt.
     * Wird als Enumeration gespeichert.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    /**
     * Token für Passwort-Reset-Funktionalität.
     * Wird bei JSON-Serialisierung ignoriert aus Sicherheitsgründen.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private PasswordResetToken resetToken;

    /**
     * Enumeration für die verschiedenen Benutzertypen im System.
     * <p>
     * Jeder Typ hat spezifische Berechtigungen und Funktionalitäten:
     * - STUDENT: Kann Noten einsehen, Stundenplan anzeigen, Cafeteria-Bestellungen verfolgen
     * - PARENT: Kann Informationen über Kinder einsehen, Cafeteria-Bestellungen aufgeben
     * - TEACHER: Kann Noten vergeben, Anwesenheit verwalten, Stundenpläne erstellen
     * - CHEF: Kann Menüs verwalten, Bestellungen bearbeiten
     * - ADMIN: Hat vollständige Systemzugriff und Benutzerverwaltung
     */
    public enum UserType {
        /**
         * Schüler - Kann eigene Daten einsehen und begrenzte Interaktionen durchführen.
         */
        STUDENT,

        /**
         * Eltern - Kann Informationen über Kinder einsehen und Bestellungen aufgeben.
         */
        PARENT,

        /**
         * Lehrer - Kann Noten und Anwesenheit verwalten, Stundenpläne erstellen.
         */
        TEACHER,

        /**
         * Koch - Kann Cafeteria-Menüs und Bestellungen verwalten.
         */
        CHEF,

        /**
         * Administrator - Hat vollständige Systemrechte und Benutzerverwaltung.
         */
        ADMIN
    }
}