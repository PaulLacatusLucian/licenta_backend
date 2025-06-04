package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.repositories.ClassSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClassSessionService {

    @Autowired
    private ClassSessionRepository classSessionRepository;

    // Hinzufügen einer Unterrichtseinheit
    public ClassSession addClassSession(ClassSession classSession) {
        return classSessionRepository.save(classSession);
    }

    // Alle Unterrichtseinheiten abrufen
    public List<ClassSession> getAllClassSessions() {
        return classSessionRepository.findAll();
    }

    // Unterrichtseinheiten nach Lehrer-ID abrufen
    public List<ClassSession> getSessionsByTeacher(Long teacherId) {
        return classSessionRepository.findByTeacherId(teacherId);
    }

    // Unterrichtseinheiten nach Fach abrufen
    public List<ClassSession> getSessionsBySubject(String subject) {
        return classSessionRepository.findBySubject(subject);
    }

    // Unterrichtseinheiten innerhalb eines Zeitintervalls abrufen
    public List<ClassSession> getSessionsByTimeInterval(LocalDateTime start, LocalDateTime end) {
        return classSessionRepository.findByStartTimeBetween(start, end);
    }

    // Unterrichtseinheit löschen
    public void deleteClassSession(Long id) {
        classSessionRepository.deleteById(id);
    }

    // Unterrichtseinheit nach ID abrufen, mit Fehlermeldung wenn nicht gefunden
    public ClassSession getSessionById(Long sessionId) {
        return classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Die Unterrichtseinheit wurde nicht gefunden."));
    }

}
