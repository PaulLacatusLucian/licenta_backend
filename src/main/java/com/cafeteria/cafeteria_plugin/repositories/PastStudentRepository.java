package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.PastStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository-Interface für absolventenspezifische Datenbankoperationen.
 * <p>
 * Diese Schnittstelle erweitert JpaRepository und stellt spezialisierte
 * CRUD-Operationen für ehemalige Schüler (Absolventen) bereit. PastStudents
 * repräsentieren Schüler, die das Schulsystem erfolgreich abgeschlossen haben
 * oder anderweitig verlassen haben.
 * <p>
 * Das Repository unterstützt:
 * - Standard CRUD-Operationen für Absolventen
 * - Archivierung von Schülerdaten bei Abschluss
 * - Alumni-Verwaltung und -nachverfolgung
 * - Historische Datenaufbewahrung
 * - Einfache Datenspeicherung ohne komplexe Beziehungen
 * <p>
 * Besondere Merkmale:
 * - Minimalistisches Datenmodell für Archivierungszwecke
 * - Trennung von aktiven und ehemaligen Schülern
 * - Unterstützung für Jahresübergang und Graduierung
 * - Einfache Datenstruktur für langfristige Speicherung
 * - Alumni-Tracking ohne personenbezogene Details
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see PastStudent
 * @see JpaRepository
 * @since 2025-01-01
 */
@Repository
public interface PastStudentRepository extends JpaRepository<PastStudent, Long> {

    /*
     * Hinweis: Dieses Repository verwendet nur die Standard-JpaRepository-Methoden.
     *
     * Verfügbare Standard-Operationen:
     * - save(PastStudent) - Speichern neuer Absolventen
     * - findById(Long) - Suchen nach ID
     * - findAll() - Alle Absolventen abrufen
     * - deleteById(Long) - Absolventen löschen
     * - count() - Anzahl der Absolventen
     *
     * Das einfache Design reflektiert die Archivierungs-Natur der Entität.
     * Komplexere Abfragen können bei Bedarf hinzugefügt werden, z.B.:
     * - findByProfile(String) - Suche nach Profil/Spezialisierung
     * - findByNameContaining(String) - Namenssuche
     * - Zeitbasierte Abfragen nach Abschlussjahr
     */
}