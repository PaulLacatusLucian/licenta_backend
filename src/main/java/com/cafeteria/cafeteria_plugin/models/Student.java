package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Modellklasse für Schüler im Schulverwaltungssystem.
 * @author Paul Lacatus
 * @version 1.0
 * @see User
 * @see Class
 * @see Parent
 * @see Grade
 * @see Absence
 * @since 2024-12-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student extends User {

    /**
     * Vollständiger Name des Schülers.
     * Wird für Anzeige und Identifikation verwendet.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Telefonnummer des Schülers.
     * Optional für Kontaktmöglichkeiten.
     */
    private String phoneNumber;

    /**
     * Schulklasse, der der Schüler zugeordnet ist.
     *
     * Diese Many-to-One-Beziehung ermöglicht es, dass mehrere Schüler
     * derselben Klasse angehören. JsonBackReference verhindert
     * zirkuläre Referenzen bei der JSON-Serialisierung.
     */
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    @JsonBackReference
    private Class studentClass;

    /**
     * Elternteil, der mit diesem Schüler verknüpft ist.
     *
     * Diese Many-to-One-Beziehung ermöglicht die Verbindung zwischen
     * Schüler und Eltern für Kommunikation und Berechtigung.
     * JsonIgnore verhindert die Serialisierung sensibler Elterndaten.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    /**
     * URL oder Pfad zum Profilbild des Schülers.
     * Optional für personalisierte Darstellung im System.
     */
    @Column
    private String profileImage;

}