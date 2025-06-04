package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Semester;
import com.cafeteria.cafeteria_plugin.repositories.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Zentraler Service für die Semesterverwaltung im Schulverwaltungssystem.
 *
 * Diese Klasse ist verantwortlich für:
 * - Verwaltung des aktuellen Semesters
 * - Semesterübergänge und -wechsel
 * - Abruf von Semesterinformationen
 * - Koordination der schulischen Zeitperioden
 *
 * Der Service stellt sicher, dass das Schuljahr korrekt in Semestern
 * organisiert und verwaltet wird.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Semester
 * @since 2025-01-01
 */
@Service
public class SemesterService {

    /**
     * Repository für Semesteroperationen.
     */
    private final SemesterRepository semesterRepository;

    /**
     * Konstruktor für SemesterService.
     *
     * @param semesterRepository Repository für Datenbankoperationen mit Semestern
     */
    @Autowired
    public SemesterService(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    /**
     * Aktuelles Semester abrufen (wir nehmen an, dass es nur einen Datensatz gibt).
     *
     * Diese Methode ruft das aktuell aktive Semester ab. Das System
     * geht davon aus, dass immer genau ein Semester-Datensatz mit ID 1 existiert.
     *
     * @return Das aktuelle Semester
     * @throws IllegalStateException Falls das Semester nicht initialisiert wurde
     */
    public Semester getCurrentSemester() {
        return semesterRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Das Semester wurde nicht initialisiert."));
    }

    /**
     * Semester erhöhen (z. B. von 1 auf 2).
     *
     * Diese Methode führt den Übergang zum nächsten Semester durch,
     * indem die Semesternummer um 1 erhöht wird. Dies wird typischerweise
     * am Ende eines Semesters aufgerufen.
     *
     * @return Das aktualisierte Semester mit der neuen Semesternummer
     */
    public Semester incrementSemester() {
        Semester semester = getCurrentSemester();
        semester.setCurrentSemester(semester.getCurrentSemester() + 1);
        return semesterRepository.save(semester);
    }
}