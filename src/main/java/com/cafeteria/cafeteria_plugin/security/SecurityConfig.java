package com.cafeteria.cafeteria_plugin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Zentrale Spring Security-Konfiguration für das Schulverwaltungssystem.
 * @author Paul Lacatus
 * @version 1.0
 * @see JwtFilter
 * @see UserDetailsServiceImpl
 * @since 2025-03-12
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Logger für Sicherheitskonfiguration und Debugging.
     */
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * JWT-Filter für Token-Validierung bei eingehenden Anfragen.
     */
    @Autowired
    private JwtFilter jwtFilter;

    /**
     * Konfiguriert den Passwort-Encoder für sichere Passwort-Speicherung.
     * <p>
     * Verwendet BCrypt mit Strength 10, was einen guten Kompromiss zwischen
     * Sicherheit und Performance darstellt. BCrypt ist ein adaptiver
     * Hashing-Algorithmus, der gegen Rainbow-Table-Angriffe resistent ist.
     *
     * @return BCryptPasswordEncoder mit Strength 10
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Konfiguriert den AuthenticationManager für die Benutzerauthentifizierung.
     * <p>
     * Der AuthenticationManager ist verantwortlich für die Validierung
     * von Benutzeranmeldeinformationen und die Erstellung von Authentication-Objekten.
     *
     * @param authConfig Spring Security AuthenticationConfiguration
     * @return Konfigurierter AuthenticationManager
     * @throws Exception Falls die Konfiguration fehlschlägt
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Konfiguriert die Security Filter Chain für HTTP-Anfragen.
     * <p>
     * Diese Methode definiert die gesamte Sicherheitsarchitektur der Anwendung:
     * - CORS-Konfiguration für Frontend-Integration
     * - CSRF-Schutz deaktiviert (nicht erforderlich bei JWT)
     * - Stateless Session Management
     * - URL-basierte Zugriffskontrolle
     * - JWT-Filter-Integration
     * - H2-Console-Konfiguration für Entwicklung
     *
     * @param http HttpSecurity-Objekt für Konfiguration
     * @return Konfigurierte SecurityFilterChain
     * @throws Exception Falls die Konfiguration fehlschlägt
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Sicherheits-Filter-Chain wird konfiguriert");

        http
                // CORS-Unterstützung aktivieren
                .cors()
                .and()

                // CSRF-Schutz deaktivieren (nicht erforderlich bei JWT)
                .csrf(csrf -> csrf.disable())

                // Stateless Session Management (keine Server-seitigen Sessions)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL-basierte Zugriffskontrolle konfigurieren
                .authorizeHttpRequests(auth -> auth
                        // H2-Datenbank-Console für Entwicklung freigeben
//                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()

                        // Authentifizierungs-Endpunkte öffentlich zugänglich
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/auth/**")).permitAll()

                        // Bild-Ressourcen öffentlich zugänglich
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/images/**")).permitAll()

                        // Alle anderen Endpunkte erfordern Authentifizierung
                        .anyRequest().authenticated()
                )

                // JWT-Filter vor Standard-Authentication-Filter einsetzen
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // Frame-Options für H2-Console deaktivieren
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        logger.info("Sicherheits-Filter-Chain erfolgreich konfiguriert");
        return http.build();
    }
}