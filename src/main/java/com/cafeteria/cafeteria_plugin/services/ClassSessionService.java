package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.repositories.ClassSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Zentraler Service für die Verwaltung von Unterrichtsstunden im Schulverwaltungssystem.
 * @author Paul Lacatus
 * @version 1.0
 * @see ClassSession
 * @since 2025-01-19
 */
@Service
public class ClassSessionService {

    /**
     * Repository für Unterrichtsstunden-Operationen.
     */
    @Autowired
    private ClassSessionRepository classSessionRepository;

    /**
     * Hinzufügen einer Unterrichtseinheit.
     *
     * Diese Methode speichert eine neue Unterrichtsstunde in der Datenbank
     * mit allen zugehörigen Informationen wie Lehrer, Fach und Zeitraum.
     *
     * @param classSession Die zu erstellende Unterrichtsstunde
     * @return Die gespeicherte Unterrichtsstunde mit generierter ID
     */
    public ClassSession addClassSession(ClassSession classSession) {
        return classSessionRepository.save(classSession);
    }

    /**
     * Alle Unterrichtseinheiten abrufen.
     *
     * @return Liste aller Unterrichtsstunden im System
     */
    public List<ClassSession> getAllClassSessions() {
        return classSessionRepository.findAll();
    }

    /**
     * Unterrichtseinheiten nach Lehrer-ID abrufen.
     *
     * Diese Methode ermöglicht es, alle Stunden zu finden, die ein
     * bestimmter Lehrer unterrichtet.
     *
     * @param teacherId Die ID des Lehrers
     * @return Liste der Unterrichtsstunden des Lehrers
     */
    public List<ClassSession> getSessionsByTeacher(Long teacherId) {
        return classSessionRepository.findByTeacherId(teacherId);
    }

    /**
     * Unterrichtseinheiten nach Fach abrufen.
     *
     * Diese Methode ermöglicht es, alle Stunden eines bestimmten
     * Fachs zu finden, unabhängig vom Lehrer.
     *
     * @param subject Das Fach
     * @return Liste der Unterrichtsstunden des Fachs
     */
    public List<ClassSession> getSessionsBySubject(String subject) {
        return classSessionRepository.findBySubject(subject);
    }

    /**
     * Unterrichtseinheiten innerhalb eines Zeitintervalls abrufen.
     *
     * Diese Methode ermöglicht es, alle Unterrichtsstunden zu finden,
     * die zwischen zwei Zeitpunkten stattfinden.
     *
     * @param start Startzeitpunkt des Suchbereichs
     * @param end Endzeitpunkt des Suchbereichs
     * @return Liste der Unterrichtsstunden im angegebenen Zeitraum
     */
    public List<ClassSession> getSessionsByTimeInterval(LocalDateTime start, LocalDateTime end) {
        return classSessionRepository.findByStartTimeBetween(start, end);
    }

    /**
     * Unterrichtseinheit löschen.
     *
     * @param id Die ID der zu löschenden Unterrichtsstunde
     */
    public void deleteClassSession(Long id) {
        classSessionRepository.deleteById(id);
    }

    /**
     * Unterrichtseinheit nach ID abrufen, mit Fehlermeldung wenn nicht gefunden.
     *
     * @param sessionId Die ID der gesuchten Unterrichtsstunde
     * @return Die gefundene Unterrichtsstunde
     * @throws IllegalArgumentException Falls keine Unterrichtsstunde mit der ID gefunden wird
     */
    public ClassSession getSessionById(Long sessionId) {
        return classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Die Unterrichtseinheit wurde nicht gefunden."));
    }

}