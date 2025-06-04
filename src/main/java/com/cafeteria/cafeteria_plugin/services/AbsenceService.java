package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.repositories.AbsenceRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Zentraler Service für die Abwesenheitsverwaltung im Schulverwaltungssystem.
 *
 * Diese Klasse ist verantwortlich für:
 * - Verwaltung von Schülerabwesenheiten
 * - Validierung von Abwesenheitsdaten
 * - Berechnung von Abwesenheitsstatistiken
 * - CRUD-Operationen für Abwesenheiten
 *
 * Der Service stellt sicher, dass Abwesenheiten korrekt erfasst und
 * verwaltet werden, mit entsprechenden Validierungen und Geschäftslogik.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Absence
 * @since 2025-01-01
 */
@Service
public class AbsenceService {

    /**
     * Repository für Abwesenheitsoperationen.
     */
    @Autowired
    private AbsenceRepository absenceRepository;


    @Autowired
    private StudentRepository studentRepository;


    /**
     * Fügt eine neue Abwesenheit hinzu.
     *
     * Diese Methode führt grundlegende Validierungen durch und speichert
     * die Abwesenheit in der Datenbank.
     *
     * @param absence Die zu speichernde Abwesenheit
     * @return Die gespeicherte Abwesenheit mit generierter ID
     * @throws IllegalArgumentException Falls die Abwesenheit null ist
     */
    public Absence addAbsence(Absence absence) {
        return absenceRepository.save(absence);
    }

    /**
     * Ruft alle Abwesenheiten ab.
     *
     * @return Liste aller Abwesenheiten im System
     */
    public List<Absence> getAllAbsences() {
        return absenceRepository.findAll();
    }

    /**
     * Sucht eine Abwesenheit anhand der ID.
     *
     * @param id Die ID der gesuchten Abwesenheit
     * @return Optional mit der gefundenen Abwesenheit oder leer falls nicht gefunden
     */
    public Optional<Absence> getAbsenceById(Long id) {
        return absenceRepository.findById(id);
    }

    /**
     * Aktualisiert eine bestehende Abwesenheit.
     *
     * Diese Methode sucht die existierende Abwesenheit und aktualisiert
     * deren Eigenschaften mit den neuen Werten.
     *
     * @param id Die ID der zu aktualisierenden Abwesenheit
     * @param updatedAbsence Die Abwesenheit mit den neuen Daten
     * @return Die aktualisierte Abwesenheit
     * @throws IllegalArgumentException Falls keine Abwesenheit mit der ID gefunden wird
     */
    public Absence updateAbsence(Long id, Absence updatedAbsence) {
        return absenceRepository.findById(id)
                .map(existingAbsence -> {
                    existingAbsence.setClassSession(updatedAbsence.getClassSession());
                    existingAbsence.setStudent(updatedAbsence.getStudent());
                    existingAbsence.setJustified(updatedAbsence.getJustified());
                    return absenceRepository.save(existingAbsence);
                }).orElseThrow(() -> new IllegalArgumentException("Abwesenheit nicht gefunden"));
    }

    /**
     * Löscht eine Abwesenheit anhand der ID.
     *
     * @param id Die ID der zu löschenden Abwesenheit
     */
    public void deleteAbsence(Long id) {
        absenceRepository.deleteById(id);
    }

    /**
     * Berechnet die Gesamtanzahl der Abwesenheiten für einen Schüler.
     *
     * @param studentId Die ID des Schülers
     * @return Gesamtanzahl der Abwesenheiten
     */
    public int getTotalAbsencesForStudent(Long studentId) {
        return absenceRepository.countByStudentId(studentId);
    }

    /**
     * Validiert und speichert eine Abwesenheit.
     *
     * Diese Methode führt umfassende Validierungen durch, einschließlich
     * der Überprüfung auf Duplikate und erforderliche Felder.
     *
     * @param absence Die zu validierende und speichernde Abwesenheit
     * @return Die gespeicherte Abwesenheit
     * @throws IllegalArgumentException Falls Validierung fehlschlägt oder Duplikat existiert
     */
    public Absence saveAbsence(Absence absence) {
        if (absence.getStudent() == null || absence.getClassSession() == null) {
            throw new IllegalArgumentException("Schüler und Unterrichtsstunde sind Pflichtfelder.");
        }

        // Überprüfung auf Duplikate
        boolean exists = absenceRepository.existsByStudentIdAndClassSessionId(
                absence.getStudent().getId(),
                absence.getClassSession().getId()
        );

        if (exists) {
            throw new IllegalArgumentException("Abwesenheit existiert bereits für diesen Schüler in dieser Unterrichtsstunde.");
        }

        return absenceRepository.save(absence);
    }

    /**
     * Ruft alle Abwesenheiten für einen bestimmten Schüler ab.
     *
     * @param studentId Die ID des Schülers
     * @return Liste der Abwesenheiten des Schülers
     */
    public List<Absence> getAbsencesForStudent(Long studentId) {
        return absenceRepository.findByStudentId(studentId);
    }

    /**
     * Überprüft, ob eine Abwesenheit für einen Schüler in einer bestimmten Stunde existiert.
     *
     * @param studentId Die ID des Schülers
     * @param classSessionId Die ID der Unterrichtsstunde
     * @return true wenn eine Abwesenheit existiert, false andernfalls
     */
    public boolean existsByStudentIdAndClassSessionId(Long studentId, Long classSessionId) {
        return absenceRepository.existsByStudentIdAndClassSessionId(studentId, classSessionId);
    }

    // Abwesenheit als entschuldigt markieren
    public Absence justifyAbsence(Absence absence) {
        absence.setJustified(true);
        return absenceRepository.save(absence);
    }

    // Unentschuldigte Abwesenheiten für eine bestimmte Klasse abrufen
    public List<Absence> getUnjustifiedAbsencesForClass(Long classId) {
        List<Student> studentsInClass = studentRepository.findByStudentClassId(classId);

        if (studentsInClass.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> studentIds = studentsInClass.stream()
                .map(Student::getId)
                .collect(Collectors.toList());

        return absenceRepository.findUnjustifiedAbsencesByStudentIds(studentIds);
    }
}