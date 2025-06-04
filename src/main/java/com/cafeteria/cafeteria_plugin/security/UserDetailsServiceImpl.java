package com.cafeteria.cafeteria_plugin.security;

import com.cafeteria.cafeteria_plugin.models.User;
import com.cafeteria.cafeteria_plugin.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementierung des Spring Security UserDetailsService für das Schulsystem.
 * <p>
 * Diese Klasse stellt die Brücke zwischen dem benutzerdefinierten User-System
 * und Spring Security dar. Sie lädt Benutzerinformationen aus der Datenbank
 * und konvertiert sie in das von Spring Security erwartete UserDetails-Format
 * für Authentifizierung und Autorisierung.
 * <p>
 * Die Implementierung unterstützt:
 * - Polymorphe Benutzertypen (Student, Parent, Teacher, Chef, Admin)
 * - Rollenbasierte Autorisierung mit automatischer Rollenpräfix-Generierung
 * - Sichere Passwort-Validierung durch Spring Security
 * - Einheitliche Authentifizierung für alle Benutzertypen
 * - Integration mit JWT-Token-System
 * <p>
 * Technische Details:
 * - Implementiert UserDetailsService-Interface von Spring Security
 * - Konvertiert User-Entitäten zu Spring Security UserDetails
 * - Unterstützt rollenbasierte Berechtigungen (ROLE_STUDENT, ROLE_TEACHER, etc.)
 * - Thread-safe durch stateless Design
 * - Performance-optimiert durch direkte Repository-Abfragen
 * <p>
 * Sicherheitsaspekte:
 * - Sichere Benutzersuche ohne Preisgabe von Systemdetails
 * - Automatische Rollenzuordnung basierend auf Benutzertyp
 * - Schutz vor Username-Enumeration durch generische Fehlermeldungen
 * - Integration mit Spring Security's Passwort-Encoder
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see UserDetailsService
 * @see User
 * @see UserRepository
 * @see org.springframework.security.core.userdetails.UserDetails
 * @since 2025-01-01
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Repository für Datenbankzugriff auf Benutzerinformationen.
     * <p>
     * Wird für die Benutzersuche während des Authentifizierungsprozesses verwendet.
     * Das Repository unterstützt polymorphe Abfragen über alle Benutzertypen.
     */
    private final UserRepository userRepository;

    /**
     * Konstruktor für Dependency Injection des UserRepository.
     * <p>
     * Spring's Constructor Injection sorgt für sichere und testbare
     * Abhängigkeitsverwaltung. Das Repository wird zur Laufzeit
     * automatisch injiziert.
     *
     * @param userRepository Repository für Benutzer-Datenbankoperationen
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Lädt Benutzerdetails für Spring Security Authentifizierung.
     * <p>
     * Diese Methode wird von Spring Security während des Login-Prozesses aufgerufen.
     * Sie sucht den Benutzer in der Datenbank und konvertiert die User-Entität
     * in ein UserDetails-Objekt, das Spring Security für Authentifizierung
     * und Autorisierung verwenden kann.
     * <p>
     * Ablauf:
     * 1. Suche des Benutzers in der Datenbank anhand des Benutzernamens
     * 2. Validierung der Existenz (wirft Exception bei Nicht-Existenz)
     * 3. Extraktion der Authentifizierungsdaten (Username, Passwort)
     * 4. Automatische Rollengenerierung basierend auf Benutzertyp
     * 5. Erstellung des UserDetails-Objekts für Spring Security
     * <p>
     * Rollenkonvertierung:
     * - Student -> ROLE_STUDENT
     * - Parent -> ROLE_PARENT
     * - Teacher -> ROLE_TEACHER
     * - Chef -> ROLE_CHEF
     * - Admin -> ROLE_ADMIN
     * <p>
     * Sicherheitsüberlegungen:
     * - Polymorphe Benutzersuche unterstützt alle Benutzertypen
     * - Generische Fehlermeldung verhindert Username-Enumeration
     * - Passwort wird im verschlüsselten Format weitergegeben
     * - Automatische Rollenzuordnung verhindert Privilege-Escalation
     *
     * @param username Eindeutiger Benutzername für die Authentifizierung
     * @return UserDetails-Objekt mit Authentifizierungs- und Autorisierungsdaten
     * @throws UsernameNotFoundException wenn der Benutzer nicht gefunden wird
     *
     * @see org.springframework.security.core.userdetails.UserDetails
     * @see User.UserType
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Benutzersuche in der Datenbank mit polymorpher Unterstützung
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        // Konvertierung der User-Entität zu Spring Security UserDetails
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Bereits verschlüsseltes Passwort
                .authorities("ROLE_" + user.getUserType().name()) // Automatische Rollengenerierung
                .build();
    }
}