package com.cafeteria.cafeteria_plugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

/**
 * Hauptanwendungsklasse für das Cafeteria-Plugin-System.
 * <p>
 * Diese Klasse dient als Einstiegspunkt für die Spring Boot-Anwendung,
 * die ein umfassendes Schulverwaltungssystem mit Cafeteria-Funktionalitäten bereitstellt.
 * <p>
 * Das System unterstützt:
 * - Benutzerverwaltung (Schüler, Eltern, Lehrer, Köche, Administratoren)
 * - Stundenplanverwaltung und Klassensitzungen
 * - Noten- und Anwesenheitsverfolgung
 * - Cafeteria-Menü und Bestellsystem
 * - Email-Benachrichtigungen und Passwort-Reset
 *
 * @author Paul Lacatus
 * @version 1.0
 * @since 2024-11-28
 */
@SpringBootApplication
public class CafeteriaPluginApplication {

    /**
     * Hauptmethode zur Ausführung der Spring Boot-Anwendung.
     * <p>
     * Diese Methode startet den eingebetteten Tomcat-Server und initialisiert
     * alle Spring-Komponenten und Beans.
     *
     * @param args Kommandozeilenargumente (derzeit nicht verwendet)
     */
    public static void main(String[] args) {
        SpringApplication.run(CafeteriaPluginApplication.class, args);
    }
}