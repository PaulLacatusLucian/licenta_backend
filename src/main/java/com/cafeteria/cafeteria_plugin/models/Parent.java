package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Modellklasse für Eltern im Schulverwaltungssystem.
 *
 * Diese Klasse erweitert die abstrakte User-Klasse und repräsentiert die Eltern
 * oder Erziehungsberechtigten von Schülern. Sie ermöglicht die Verwaltung
 * sowohl von Mutter- als auch Vaterinformationen in einem einzigen Account.
 *
 * Funktionalitäten für Eltern:
 * - Einsicht in Noten und Anwesenheit ihrer Kinder
 * - Kommunikation mit Lehrern
 * - Cafeteria-Bestellungen für ihre Kinder
 * - Terminvereinbarungen und Elterngespräche
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see User
 * @see Student
 * @see OrderHistory
 * @since 2024-12-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parents")
public class Parent extends User {

    /**
     * Vollständiger Name der Mutter.
     * Hauptkontaktperson für den Account.
     */
    @Column(nullable = false)
    private String motherName;

    /**
     * Email-Adresse der Mutter.
     * Muss systemweit eindeutig sein und wird für Benachrichtigungen verwendet.
     */
    @Column(unique = true)
    private String motherEmail;

    /**
     * Telefonnummer der Mutter für dringende Kontaktaufnahme.
     */
    private String motherPhoneNumber;

    /**
     * Vollständiger Name des Vaters.
     * Optionale zusätzliche Kontaktperson.
     */
    private String fatherName;

    /**
     * Email-Adresse des Vaters.
     * Muss systemweit eindeutig sein, falls angegeben.
     */
    @Column(unique = true)
    private String fatherEmail;

    /**
     * Telefonnummer des Vaters für Kontaktaufnahme.
     */
    private String fatherPhoneNumber;

    /**
     * URL oder Pfad zum Profilbild der Eltern.
     * Optional für personalisierte Darstellung im System.
     */
    @Column
    private String profileImage;

    /**
     * Liste aller Kinder, die diesem Eltern-Account zugeordnet sind.
     *
     * Diese One-to-Many-Beziehung ermöglicht es einem Eltern-Account,
     * mehrere Kinder zu verwalten. JsonIgnore verhindert zirkuläre
     * Referenzen bei der JSON-Serialisierung.
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Student> students;
}