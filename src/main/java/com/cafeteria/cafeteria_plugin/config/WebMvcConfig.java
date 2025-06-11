package com.cafeteria.cafeteria_plugin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Konfigurationsklasse für Web MVC-Einstellungen im Schulverwaltungssystem.
 *
 * Diese Klasse ist verantwortlich für:
 * - Konfiguration der statischen Ressourcen-Handler
 * - Mapping von Upload-Verzeichnissen für Bilderdateien
 * - Bereitstellung von Dateien über HTTP-Endpunkte
 * - Verwaltung der Dateipfad-Konfiguration
 *
 * Die Konfiguration ermöglicht es, hochgeladene Bilder über
 * Web-URLs zugänglich zu machen.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see WebMvcConfigurer
 * @since 2024-11-28
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Verzeichnispfad für hochgeladene Bilder.
     * Wird aus der Konfigurationsdatei (application.properties) gelesen.
     */
    @Value("${image.upload.dir}")
    private String uploadDir;

    /**
     * Fügt Resource-Handler für statische Dateien hinzu.
     *
     * Diese Methode konfiguriert das Mapping zwischen Web-URLs und
     * dem lokalen Dateisystem für hochgeladene Bilder:
     * - URLs mit "/images/**" werden auf das konfigurierte Upload-Verzeichnis gemappt
     * - Absolute Pfade werden korrekt für verschiedene Betriebssysteme formatiert
     * - Trailing Slashes werden automatisch hinzugefügt wenn nötig
     *
     * Beispiel: "/images/profile.jpg" → "file:///C:/uploads/profile.jpg"
     *
     * @param registry Die ResourceHandlerRegistry zur Konfiguration der Handler
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File directory = new File(uploadDir);
        String absolutePath = directory.getAbsolutePath();

        String resourceLocation = "file:///" + absolutePath.replace("\\", "/");
        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }

        System.out.println("Bilder-Resource-Handler wird konfiguriert mit Pfad: " + resourceLocation);

        registry.addResourceHandler("/images/**")
                .addResourceLocations(resourceLocation);
    }
}