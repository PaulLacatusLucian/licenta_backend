package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository-Interface für elternspezifische Datenbankoperationen.
 * @author Paul Lacatus
 * @version 1.0
 * @see Parent
 * @see Class
 * @see JpaRepository
 * @since 2024-12-18
 */
public interface ParentRepository extends JpaRepository<Parent, Long> {

    /**
     * Sucht einen Elternteil anhand der Mutter-E-Mail-Adresse.
     * <p>
     * Diese Methode ermöglicht die Identifikation von Elternkonten über
     * die E-Mail-Adresse der Mutter. Sie wird für Authentifizierung,
     * Passwort-Reset und Kommunikation verwendet.
     * <p>
     * Verwendung:
     * - Passwort-Reset-Funktionen
     * - E-Mail-basierte Benutzersuche
     * - Kommunikationssystem-Integration
     * - Authentifizierungs-Validierung
     *
     * @param motherEmail Die E-Mail-Adresse der Mutter
     * @return Optional mit dem Elternteil falls vorhanden, leer falls nicht gefunden
     */
    Optional<Parent> findByMotherEmail(String motherEmail);

    /**
     * Sucht einen Elternteil anhand der Vater-E-Mail-Adresse.
     * <p>
     * Diese Methode ermöglicht die Identifikation von Elternkonten über
     * die E-Mail-Adresse des Vaters. Sie bietet alternative Kontaktmöglichkeiten
     * und unterstützt flexible Familienkommunikation.
     * <p>
     * Verwendung:
     * - Alternative Kontaktaufnahme
     * - Passwort-Reset für Väter
     * - Duale Eltern-Kommunikation
     * - Flexible Authentifizierung
     *
     * @param fatherEmail Die E-Mail-Adresse des Vaters
     * @return Optional mit dem Elternteil falls vorhanden, leer falls nicht gefunden
     */
    Optional<Parent> findByFatherEmail(String fatherEmail);

    /**
     * Ermittelt alle Eltern von Schülern einer bestimmten Klasse.
     * <p>
     * Diese komplexe Query navigiert über die Schüler-Eltern-Beziehung
     * und sammelt alle eindeutigen Elternkonten einer Klasse. Sie wird
     * für Klassenkommunikation und Elternversammlungen verwendet.
     * <p>
     * Technische Details:
     * - JOIN über students und studentClass Entitäten
     * - DISTINCT um Duplikate zu vermeiden
     * - Navigation über mehrere Entitätsebenen
     * <p>
     * Verwendung:
     * - Klassenelternabende und -versammlungen
     * - Rundschreiben und Klassenkommunikation
     * - Elternlisten für Lehrer
     * - Event-Management und Einladungen
     *
     * @param classId Eindeutige ID der Schulklasse
     * @return Liste aller Eltern von Schülern in der angegebenen Klasse
     */
    @Query("SELECT DISTINCT p FROM Parent p JOIN p.students s WHERE s.studentClass.id = :classId")
    List<Parent> findDistinctByStudents_StudentClass_Id(@Param("classId") Long classId);

    /**
     * Sucht einen Elternteil anhand des Benutzernamens.
     * <p>
     * Diese Methode wird für JWT-basierte Authentifizierung verwendet,
     * um den Eltern-Kontext aus dem Token zu laden. Sie ist essentiell
     * für alle Self-Service-Funktionen der Eltern.
     * <p>
     * Verwendung:
     * - JWT-Token-Validierung und Kontextladung
     * - Session-Management
     * - Eltern-Dashboard-Zugriff
     * - Autorisierungsprüfungen
     *
     * @param username Eindeutiger Benutzername des Elternteils
     * @return Der gefundene Elternteil oder null falls nicht vorhanden
     */
    Parent findByUsername(String username);
}