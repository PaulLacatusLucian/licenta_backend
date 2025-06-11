package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.PastStudent;
import com.cafeteria.cafeteria_plugin.repositories.PastStudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Zentraler Service für die Verwaltung ehemaliger Schüler im Schulverwaltungssystem.
 * @author Paul Lacatus
 * @version 1.0
 * @see PastStudent
 * @since 2025-01-15
 */
@Service
public class PastStudentService {

    /**
     * Repository für ehemalige Schüler-Operationen.
     */
    private final PastStudentRepository pastStudentRepository;

    /**
     * Konstruktor für PastStudentService.
     *
     * @param pastStudentRepository Repository für Datenbankoperationen mit ehemaligen Schülern
     */
    public PastStudentService(PastStudentRepository pastStudentRepository) {
        this.pastStudentRepository = pastStudentRepository;
    }

    /**
     * Alle ehemaligen Schüler abrufen.
     *
     * @return Liste aller ehemaligen Schüler im System
     */
    public List<PastStudent> getAllPastStudents() {
        return pastStudentRepository.findAll();
    }

    /**
     * Ehemaligen Schüler nach ID abrufen.
     *
     * @param id Die ID des gesuchten ehemaligen Schülers
     * @return Der gefundene ehemalige Schüler
     * @throws RuntimeException Falls ehemaliger Schüler nicht gefunden wird
     */
    public PastStudent getPastStudentById(Long id) {
        return pastStudentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ehemaliger Schüler mit der ID " + id + " wurde nicht gefunden."));
    }

    /**
     * Ehemaligen Schüler speichern.
     *
     * Diese Methode wird verwendet, um einen neuen ehemaligen Schüler
     * zu archivieren oder bestehende Daten zu aktualisieren.
     *
     * @param pastStudent Der zu speichernde ehemalige Schüler
     * @return Der gespeicherte ehemalige Schüler mit generierter ID
     */
    public PastStudent savePastStudent(PastStudent pastStudent) {
        return pastStudentRepository.save(pastStudent);
    }

    /**
     * Ehemaligen Schüler löschen.
     *
     * @param id Die ID des zu löschenden ehemaligen Schülers
     */
    public void deletePastStudent(Long id) {
        pastStudentRepository.deleteById(id);
    }
}