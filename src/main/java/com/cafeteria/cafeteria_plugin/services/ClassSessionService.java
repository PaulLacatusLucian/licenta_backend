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

    // Adăugarea unei sesiuni de curs
    public ClassSession addClassSession(ClassSession classSession) {
        return classSessionRepository.save(classSession);
    }

    // Găsirea tuturor sesiunilor
    public List<ClassSession> getAllClassSessions() {
        return classSessionRepository.findAll();
    }

    // Găsirea sesiunilor după profesor
    public List<ClassSession> getSessionsByTeacher(Long teacherId) {
        return classSessionRepository.findByTeacherId(teacherId);
    }

    // Găsirea sesiunilor după materie
    public List<ClassSession> getSessionsBySubject(String subject) {
        return classSessionRepository.findBySubject(subject);
    }

    // Găsirea sesiunilor dintr-un interval de timp
    public List<ClassSession> getSessionsByTimeInterval(LocalDateTime start, LocalDateTime end) {
        return classSessionRepository.findByStartTimeBetween(start, end);
    }

    // Ștergerea unei sesiuni de curs
    public void deleteClassSession(Long id) {
        classSessionRepository.deleteById(id);
    }

    public ClassSession getSessionById(Long sessionId) {
        return classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Sesiunea nu a fost găsită."));
    }

}
