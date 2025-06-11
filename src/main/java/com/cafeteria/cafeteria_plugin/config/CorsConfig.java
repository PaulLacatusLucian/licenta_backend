package com.cafeteria.cafeteria_plugin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Konfigurationsklasse für Cross-Origin Resource Sharing (CORS) im Schulverwaltungssystem.
 *
 * Diese Klasse ist verantwortlich für:
 * - Konfiguration der CORS-Richtlinien für die Web-API
 * - Festlegung erlaubter Origins, Methoden und Headers
 * - Ermöglichung der Frontend-Backend-Kommunikation
 * - Sicherstellung der korrekten Cross-Origin-Anfragen
 *
 * Die Konfiguration ermöglicht es dem Frontend (React/Vue/Angular),
 * sicher mit dem Backend zu kommunizieren.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see WebMvcConfigurer
 * @since 2024-12-02
 */
@Configuration
public class CorsConfig {

    /**
     * Erstellt und konfiguriert den CORS-Konfigurator für die Anwendung.
     *
     * Diese Methode definiert die CORS-Richtlinien für alle API-Endpunkte:
     * - Erlaubt Anfragen vom lokalen Entwicklungsserver (localhost:5173)
     * - Unterstützt alle Standard-HTTP-Methoden
     * - Ermöglicht Authorization- und Content-Type-Headers
     * - Exponiert Authorization-Header für Frontend-Zugriff
     *
     * @return WebMvcConfigurer mit konfigurierten CORS-Einstellungen
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * Fügt CORS-Mappings zur Registrierung hinzu.
             *
             * Diese Methode konfiguriert die spezifischen CORS-Einstellungen:
             * - Alle Endpunkte ("/**") sind für CORS-Anfragen verfügbar
             * - Nur localhost:5173 ist als Origin erlaubt (Frontend-Development-Server)
             * - Alle Standard-REST-Methoden sind erlaubt
             * - Authorization und Content-Type Headers sind erlaubt
             * - Authorization Header wird für Frontend-Zugriff exponiert
             *
             * @param registry Die CORS-Registrierung zur Konfiguration
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Authorization", "Content-Type")
                        .exposedHeaders("Authorization");
            }
        };
    }
}