package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.dtos.GradeDTO;
import com.cafeteria.cafeteria_plugin.mappers.GradeMapper;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Grade;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.repositories.ClassSessionRepository;
import com.cafeteria.cafeteria_plugin.repositories.GradeRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Zentraler Service für die Notenverwaltung im Schulverwaltungssystem.
 *
 * Diese Klasse ist verantwortlich für:
 * - Erstellung und Verwaltung von Schülernoten
 * - Validierung von Notendaten
 * - Abruf von Noten nach verschiedenen Kriterien
 * - Umwandlung zwischen Entitäten und DTOs
 *
 * Der Service stellt sicher, dass alle Noten korrekt erfasst,
 * validiert und verwaltet werden.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Grade
 * @see GradeDTO
 * @since 2025-01-01
 */
@Service
public class GradeService {

    /**
     * Repository für Notenoperationen.
     */
    @Autowired
    private GradeRepository gradeRepository;

    /**
     * Mapper für die Konvertierung zwischen Grade-Entität und DTO.
     */
    @Autowired
    private GradeMapper gradeMapper;

    /**
     * Repository für Schüleroperationen.
     */
    @Autowired
    private StudentRepository studentRepository;

    /**
     * Repository für Unterrichtsstunden-Operationen.
     */
    @Autowired
    private ClassSessionRepository classSessionRepository;

    /**
     * Neue Note speichern.
     *
     * Diese Methode speichert eine neue Note in der Datenbank.
     *
     * @param grade Die zu speichernde Note
     * @return Die gespeicherte Note mit generierter ID
     */
    public Grade addGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    /**
     * Alle Noten abrufen.
     *
     * @return Liste aller Noten im System
     */
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    /**
     * Einzelne Note nach ID abrufen.
     *
     * @param id Die ID der gesuchten Note
     * @return Optional mit der gefundenen Note oder leer falls nicht gefunden
     */
    public Optional<Grade> getGradeById(Long id) {
        return gradeRepository.findById(id);
    }

    /**
     * Vorhandene Note aktualisieren.
     *
     * Diese Methode sucht die existierende Note und aktualisiert
     * alle relevanten Felder mit den neuen Werten.
     *
     * @param id Die ID der zu aktualisierenden Note
     * @param updatedGrade Die Note mit den neuen Daten
     * @return Die aktualisierte Note
     * @throws IllegalArgumentException Falls keine Note mit der ID gefunden wird
     */
    public Grade updateGrade(Long id, Grade updatedGrade) {
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Die Note mit der angegebenen ID existiert nicht."));

        existingGrade.setGrade(updatedGrade.getGrade());
        existingGrade.setStudent(updatedGrade.getStudent());
        existingGrade.setClassSession(updatedGrade.getClassSession());
        existingGrade.setDescription(updatedGrade.getDescription());

        return gradeRepository.save(existingGrade);
    }

    /**
     * Note löschen.
     *
     * @param id Die ID der zu löschenden Note
     */
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }

    /**
     * Note speichern (alternativ).
     *
     * @param grade Die zu speichernde Note
     * @return Die gespeicherte Note
     */
    public Grade saveGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    /**
     * Alle Noten eines bestimmten Schülers abrufen, konvertiert in DTOs.
     *
     * Diese Methode konvertiert die Noten-Entitäten in DTOs mit
     * allen relevanten Informationen wie Lehrername, Fach und Datum.
     *
     * @param studentId Die ID des Schülers
     * @return Liste der Noten-DTOs des Schülers
     */
    public List<GradeDTO> getGradesByStudent(Long studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        return grades.stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Schüler nach ID abrufen.
     *
     * @param studentId Die ID des gesuchten Schülers
     * @return Der gefundene Schüler
     * @throws IllegalArgumentException Falls Schüler nicht gefunden wird
     */
    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Schüler nicht gefunden."));
    }

    /**
     * Überprüfen, ob eine Note bereits für die gegebene Unterrichtseinheit existiert.
     *
     * @param studentId Die ID des Schülers
     * @param classSessionId Die ID der Unterrichtsstunde
     * @return true wenn eine Note existiert, false andernfalls
     */
    public boolean existsByStudentIdAndClassSessionId(Long studentId, Long classSessionId) {
        return gradeRepository.existsByStudentIdAndClassSessionId(studentId, classSessionId);
    }

    /**
     * Note nach ID suchen.
     *
     * @param id Die ID der gesuchten Note
     * @return Optional mit der gefundenen Note oder leer falls nicht gefunden
     */
    public Optional<Grade> findById(Long id) {
        return gradeRepository.findById(id);
    }
}