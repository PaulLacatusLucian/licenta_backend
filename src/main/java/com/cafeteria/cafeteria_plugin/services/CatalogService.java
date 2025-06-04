package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.repositories.CatalogEntryRepository;
import com.cafeteria.cafeteria_plugin.repositories.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.Class;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Zentraler Service für die Katalogverwaltung im Schulverwaltungssystem.
 *
 * Diese Klasse ist verantwortlich für:
 * - Erstellung und Verwaltung von Klassenkatalogen
 * - Hinzufügung von Noten und Abwesenheiten zum Katalog
 * - Abruf von Katalogeinträgen für Klassen und Schüler
 * - Verwaltung und Aktualisierung von Katalogdaten
 *
 * Der Service stellt sicher, dass alle schulischen Leistungen und
 * Abwesenheiten korrekt im Katalog erfasst und verwaltet werden.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Catalog
 * @see CatalogEntry
 * @see EntryType
 * @since 2025-01-01
 */
@Service
public class CatalogService {

    /**
     * Repository für Katalogoperationen.
     */
    @Autowired
    private CatalogRepository catalogRepository;

    /**
     * Repository für Katalogeintragsoperationen.
     */
    @Autowired
    private CatalogEntryRepository catalogEntryRepository;

    /**
     * Service für Klassenoperationen.
     */
    @Autowired
    private ClassService classService;

    /**
     * Erstellt einen neuen Katalog für eine gegebene Klasse.
     *
     * Diese Methode erstellt einen neuen, leeren Katalog für die
     * angegebene Klasse und speichert ihn in der Datenbank.
     *
     * @param studentClass Die Klasse, für die der Katalog erstellt wird
     * @return Der erstellte und gespeicherte Katalog
     */
    public Catalog createCatalogForClass(com.cafeteria.cafeteria_plugin.models.Class studentClass) {
        Catalog catalog = new Catalog();
        catalog.setStudentClass(studentClass);
        return catalogRepository.save(catalog);
    }

    /**
     * Gibt alle Katalogeinträge für eine bestimmte Klasse zurück.
     *
     * Diese Methode sucht den Katalog für die angegebene Klasse und
     * gibt alle zugehörigen Einträge zurück.
     *
     * @param classId Die ID der Klasse
     * @return Liste der Katalogeinträge oder leere Liste falls kein Katalog existiert
     */
    public List<CatalogEntry> getCatalogEntriesForClass(Long classId) {
        Optional<Catalog> catalog = catalogRepository.findByStudentClass_Id(classId);
        if (catalog.isPresent()) {
            return catalogEntryRepository.findByCatalog_Id(catalog.get().getId());
        }
        return new ArrayList<>();
    }

    /**
     * Gibt alle Katalogeinträge für einen bestimmten Schüler zurück.
     *
     * Diese Methode ruft alle Katalogeinträge (Noten und Abwesenheiten)
     * für den angegebenen Schüler ab.
     *
     * @param studentId Die ID des Schülers
     * @return Liste aller Katalogeinträge des Schülers
     */
    public List<CatalogEntry> getStudentEntries(Long studentId) {
        return catalogEntryRepository.findByStudent_Id(studentId);
    }

    /**
     * Fügt einen Noteneintrag für einen Schüler hinzu.
     *
     * Diese Methode erstellt einen neuen Katalogeintrag für eine Note
     * und speichert ihn im entsprechenden Klassenkatalog.
     *
     * @param student Der Schüler, der die Note erhalten hat
     * @param subject Das Fach, in dem die Note erteilt wurde
     * @param value Der Notenwert
     * @return Der erstellte Katalogeintrag
     * @throws RuntimeException Falls kein Katalog für die Klasse des Schülers existiert
     */
    public CatalogEntry addGradeEntry(Student student, String subject, Double value) {
        // Sucht den Katalog der Klasse des Schülers
        Optional<Catalog> catalogOpt = catalogRepository.findByStudentClass_Id(student.getStudentClass().getId());
        if (!catalogOpt.isPresent()) {
            throw new RuntimeException("Katalog wurde für die Klasse nicht gefunden: " + student.getStudentClass().getName());
        }

        CatalogEntry entry = new CatalogEntry();
        entry.setCatalog(catalogOpt.get());
        entry.setStudent(student);
        entry.setSubject(subject);
        entry.setType(EntryType.GRADE);
        entry.setGradeValue(value);
        entry.setDate(LocalDateTime.now());

        return catalogEntryRepository.save(entry);
    }

    /**
     * Fügt einen Abwesenheitseintrag für einen Schüler hinzu.
     *
     * Diese Methode erstellt einen neuen Katalogeintrag für eine Abwesenheit
     * und speichert ihn im entsprechenden Klassenkatalog.
     *
     * @param student Der abwesende Schüler
     * @param subject Das Fach, in dem der Schüler abwesend war
     * @param justified Ob die Abwesenheit entschuldigt ist
     * @param absenceId Die ID der zugehörigen Abwesenheit
     * @return Der erstellte Katalogeintrag
     * @throws RuntimeException Falls kein Katalog für die Klasse des Schülers existiert
     */
    public CatalogEntry addAbsenceEntry(Student student, String subject, Boolean justified, Long absenceId) {
        Optional<Catalog> catalogOpt = catalogRepository.findByStudentClass_Id(student.getStudentClass().getId());
        if (!catalogOpt.isPresent()) {
            throw new RuntimeException("Katalog wurde für die Klasse nicht gefunden: " + student.getStudentClass().getName());
        }

        CatalogEntry entry = new CatalogEntry();
        entry.setCatalog(catalogOpt.get());
        entry.setStudent(student);
        entry.setSubject(subject);
        entry.setType(EntryType.ABSENCE);
        entry.setJustified(justified);
        entry.setDate(LocalDateTime.now());
        entry.setAbsenceId(absenceId);

        return catalogEntryRepository.save(entry);
    }

    /**
     * Aktualisiert die Entschuldigung einer Abwesenheit basierend auf der Abwesenheits-ID.
     *
     * Diese Methode sucht den Katalogeintrag zur angegebenen Abwesenheits-ID
     * und aktualisiert den Entschuldigungsstatus.
     *
     * @param absenceId Die ID der Abwesenheit
     * @param justified Der neue Entschuldigungsstatus
     * @return Der aktualisierte Katalogeintrag
     * @throws RuntimeException Falls kein Katalogeintrag zur Abwesenheits-ID gefunden wird
     */
    public CatalogEntry updateAbsenceJustification(Long absenceId, Boolean justified) {
        Optional<CatalogEntry> entryOpt = catalogEntryRepository.findByAbsenceId(absenceId);

        if (entryOpt.isPresent()) {
            CatalogEntry entry = entryOpt.get();
            entry.setJustified(justified);
            return catalogEntryRepository.save(entry);
        } else {
            throw new RuntimeException("Kein Katalogeintrag zur Abwesenheit mit der ID gefunden: " + absenceId);
        }
    }
}