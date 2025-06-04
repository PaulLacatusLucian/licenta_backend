package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.AbsenceDTO;
import com.cafeteria.cafeteria_plugin.dtos.ClassSessionDTO;
import com.cafeteria.cafeteria_plugin.dtos.GradeDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.mappers.AbsenceMapper;
import com.cafeteria.cafeteria_plugin.mappers.ClassSessionMapper;
import com.cafeteria.cafeteria_plugin.mappers.GradeMapper;
import com.cafeteria.cafeteria_plugin.mappers.StudentMapper;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST-Controller für die Verwaltung von Unterrichtsstunden (Klassensitzungen).
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für die Administration von Unterrichtsstunden
 * und die Verwaltung damit verbundener Aktivitäten wie Noten und Abwesenheiten
 * der Schüler bereit. Sie ermöglicht die Anwesenheitserfassung, Notenvergabe
 * und Überwachung der akademischen Aktivitäten während des Unterrichts.
 * <p>
 * Hauptfunktionen:
 * - Erstellung und Verwaltung von Unterrichtsstunden
 * - Erfassung von Abwesenheiten und Noten für Schüler
 * - Filterung von Stunden nach Lehrer, Fach und Zeitraum
 * - Abruf der Schülerliste für eine spezifische Stunde
 * - Automatische Synchronisation mit dem Schulkatalog
 * - Validierung akademischer Regeln (Abwesende können keine Noten erhalten)
 * <p>
 * Sicherheit:
 * - Lehrer und Administratoren können Stunden verwalten
 * - JWT-Authentifizierung für sensible Operationen
 * - Rollenbasierte Validierung für Funktionszugriff
 * - Überprüfung der Schülerzugehörigkeit zur jeweiligen Stunde
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see ClassSessionService
 * @see ClassSession
 * @see Grade
 * @see Absence
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/class-sessions")
public class ClassSessionController {

    /**
     * Service für Unterrichtsstunden-Operationen.
     */
    @Autowired
    private ClassSessionService classSessionService;

    /**
     * Service für Notenverwaltung.
     */
    @Autowired
    private GradeService gradeService;

    /**
     * Service für Schüleroperationen.
     */
    @Autowired
    private StudentService studentService;

    /**
     * Service für Abwesenheitsverwaltung.
     */
    @Autowired
    private AbsenceService absenceService;

    /**
     * Service für Lehreroperationen.
     */
    @Autowired
    private TeacherService teacherService;

    /**
     * Mapper für Transformation von ClassSession-Entitäten in DTOs.
     */
    @Autowired
    private ClassSessionMapper classSessionMapper;

    /**
     * Mapper für Transformation von Grade-Entitäten in DTOs.
     */
    @Autowired
    private GradeMapper gradeMapper;

    /**
     * Mapper für Transformation von Absence-Entitäten in DTOs.
     */
    @Autowired
    private AbsenceMapper absenceMapper;

    /**
     * Mapper für Transformation von Student-Entitäten in DTOs.
     */
    @Autowired
    private StudentMapper studentMapper;

    /**
     * Service für Klassenoperationen.
     */
    @Autowired
    private ClassService classService;

    /**
     * Hilfsprogramm für JWT-Token-Verwaltung.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Service für Schulkatalog-Verwaltung.
     */
    @Autowired
    private CatalogService catalogService;

    /**
     * Erstellt eine neue Unterrichtsstunde.
     * <p>
     * Nur Lehrer und Administratoren können Unterrichtsstunden erstellen.
     * Die Stunde wird dem angegebenen Lehrer zugeordnet und kann Informationen
     * über das unterrichtete Fach, die Zeit und die Klasse enthalten.
     *
     * @param dto Unterrichtsstunden-Daten als DTO
     * @return ResponseEntity mit der erstellten Stunde oder Fehler bei ungültigen Daten
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ClassSessionDTO> addClassSession(@RequestBody ClassSessionDTO dto) {
        Teacher teacher = teacherService.getTeacherById(dto.getTeacher().getId());
        ClassSession session = classSessionMapper.toEntity(dto);
        session.setTeacher(teacher);
        ClassSession saved = classSessionService.addClassSession(session);
        return ResponseEntity.ok(classSessionMapper.toDto(saved));
    }

    /**
     * Ruft alle Unterrichtsstunden im System ab.
     * <p>
     * Zugänglich für Lehrer und Administratoren.
     * Gibt eine vollständige Liste aller registrierten Unterrichtsstunden zurück.
     *
     * @return ResponseEntity mit der Liste aller Unterrichtsstunden
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ClassSessionDTO>> getAllClassSessions() {
        List<ClassSessionDTO> dtos = classSessionService.getAllClassSessions()
                .stream().map(classSessionMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Ruft alle Unterrichtsstunden für einen spezifischen Lehrer ab.
     * <p>
     * Zugänglich für Lehrer und Administratoren.
     * Filtert die Stunden nach Lehrer-ID, um nur die vom jeweiligen
     * Lehrer gehaltenen Stunden anzuzeigen.
     *
     * @param teacherId ID des Lehrers, für den die Stunden gesucht werden
     * @return ResponseEntity mit der Liste der Stunden des angegebenen Lehrers
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ClassSessionDTO>> getSessionsByTeacher(@PathVariable Long teacherId) {
        List<ClassSessionDTO> dtos = classSessionService.getSessionsByTeacher(teacherId)
                .stream().map(classSessionMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Ruft alle Unterrichtsstunden für ein spezifisches Fach ab.
     * <p>
     * Zugänglich für Lehrer und Administratoren.
     * Filtert die Stunden nach Fachname, um alle diesem
     * Fach gewidmeten Stunden anzuzeigen.
     *
     * @param subject Name des Fachs, für das die Stunden gesucht werden
     * @return ResponseEntity mit der Liste der Stunden für das angegebene Fach
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<ClassSessionDTO>> getSessionsBySubject(@PathVariable String subject) {
        List<ClassSessionDTO> dtos = classSessionService.getSessionsBySubject(subject)
                .stream().map(classSessionMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Ruft Unterrichtsstunden aus einem angegebenen Zeitraum ab.
     * <p>
     * Zugänglich für Lehrer und Administratoren.
     * Ermöglicht die Filterung von Stunden zwischen zwei Daten für
     * Berichterstattung oder periodische akademische Analyse.
     *
     * @param start Beginn-Datum und -Zeit des Zeitraums
     * @param end   End-Datum und -Zeit des Zeitraums
     * @return ResponseEntity mit der Liste der Stunden aus dem angegebenen Zeitraum
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/time")
    public ResponseEntity<List<ClassSessionDTO>> getSessionsByTimeInterval(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        List<ClassSessionDTO> dtos = classSessionService.getSessionsByTimeInterval(start, end)
                .stream().map(classSessionMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Löscht eine Unterrichtsstunde aus dem System.
     * <p>
     * Nur Lehrer und Administratoren können Stunden löschen.
     * Die Operation entfernt die Stunde und alle zugehörigen Daten vollständig.
     *
     * @param id ID der zu löschenden Stunde
     * @return ResponseEntity mit No-Content-Status bei Erfolg
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassSession(@PathVariable Long id) {
        classSessionService.deleteClassSession(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Fügt eine Abwesenheit für einen Schüler zu einer spezifischen Stunde hinzu.
     * <p>
     * Markiert einen Schüler als abwesend in einer bestimmten Stunde.
     * Validiert, dass der Schüler zur Stunde gehört und noch keine Note erhalten hat.
     * Die Abwesenheit wird automatisch im Schulkatalog registriert.
     * <p>
     * Validierungsregeln:
     * - Schüler kann nicht als abwesend markiert werden, wenn er bereits eine Note hat
     * - Schüler muss zur Klasse gehören, die an der Stunde teilnimmt
     * - Doppelte Abwesenheit für denselben Schüler ist nicht möglich
     *
     * @param sessionId ID der Unterrichtsstunde
     * @param studentId ID des als abwesend zu markierenden Schülers
     * @param token     JWT-Authentifizierungs-Token
     * @return ResponseEntity mit der erstellten Abwesenheit oder Fehlermeldung
     */
    @PostMapping("/session/{sessionId}/absences")
    public ResponseEntity<?> addAbsenceToSession(
            @PathVariable Long sessionId,
            @RequestParam Long studentId,
            @RequestHeader("Authorization") String token) {
        try {
            // Extrahiere Lehrer aus Token
            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Teacher teacher = teacherService.findByUsername(username);

            ClassSession session = classSessionService.getSessionById(sessionId);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sitzung nicht gefunden");
            }

            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schüler nicht gefunden");
            }

            // Überprüfung: Hat bereits eine Note, kann nicht als abwesend markiert werden
            boolean hasGrade = gradeService.existsByStudentIdAndClassSessionId(studentId, sessionId);
            if (hasGrade) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Der Schüler hat bereits eine Note und kann nicht als abwesend markiert werden.");
            }

            // Überprüfung: Schüler gehört zur Klasse für diese Sitzung
            if (!isStudentInSession(student, session)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Der Schüler ist nicht für diese Sitzung eingeschrieben.");
            }

            // Überprüfe direkt, ob die Abwesenheit bereits existiert
            boolean absenceExists = absenceService.existsByStudentIdAndClassSessionId(studentId, sessionId);
            if (absenceExists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Der Schüler hat bereits eine registrierte Abwesenheit für diese Sitzung.");
            }

            Absence absence = new Absence();
            absence.setClassSession(session);
            absence.setStudent(student);
            absence.setTeacher(teacher);

            try {
                // Speichere Abwesenheit, um ID zu erhalten
                Absence savedAbsence = absenceService.saveAbsence(absence);
                AbsenceDTO dto = absenceMapper.toDto(savedAbsence);

                // Übertrage Abwesenheits-ID zum Hinzufügen in Katalog
                catalogService.addAbsenceEntry(student, session.getSubject(), false, savedAbsence.getId());

                return ResponseEntity.status(HttpStatus.CREATED).body(dto);
            } catch (IllegalArgumentException e) {
                // Diese Ausnahme wird geworfen, wenn die Abwesenheit bereits existiert
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Der Schüler hat bereits eine registrierte Abwesenheit für diese Sitzung.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Speichern der Abwesenheit: " + e.getMessage());
        }
    }

    /**
     * Fügt eine Note für einen Schüler zu einer spezifischen Stunde hinzu.
     * <p>
     * Registriert eine Note für einen Schüler in einer bestimmten Stunde.
     * Validiert, dass der Schüler nicht abwesend ist und zur jeweiligen Stunde gehört.
     * Die Note wird automatisch im Schulkatalog registriert.
     * <p>
     * Validierungsregeln:
     * - Schüler kann keine Note erhalten, wenn er als abwesend markiert ist
     * - Schüler muss zur Klasse gehören, die an der Stunde teilnimmt
     * - Note muss im gültigen Bereich liegen
     *
     * @param sessionId  ID der Unterrichtsstunde
     * @param studentId  ID des Schülers, der die Note erhält
     * @param gradeValue Wert der zu vergebenden Note
     * @return ResponseEntity mit der erstellten Note oder Fehlermeldung
     */
    @PostMapping("/session/{sessionId}/grades")
    public ResponseEntity<?> addGradeToSession(
            @PathVariable Long sessionId,
            @RequestParam Long studentId,
            @RequestParam double gradeValue) {
        try {
            ClassSession session = classSessionService.getSessionById(sessionId);
            if (session == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sitzung nicht gefunden");

            Student student = studentService.getStudentById(studentId);
            if (student == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schüler nicht gefunden");

            // Überprüfung: Wenn Schüler abwesend ist, kann er keine Note erhalten
            boolean isAbsent = absenceService.existsByStudentIdAndClassSessionId(studentId, sessionId);
            if (isAbsent) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Der Schüler ist abwesend und kann keine Note erhalten.");
            }

            // Überprüfung: Schüler gehört zur Klasse für diese Sitzung
            if (!isStudentInSession(student, session)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Der Schüler ist nicht für diese Sitzung eingeschrieben.");
            }

            Grade grade = new Grade();
            grade.setClassSession(session);
            grade.setStudent(student);
            grade.setGrade(gradeValue);
            // Notendatum wird das der Sitzung sein

            Grade savedGrade = gradeService.addGrade(grade);
            GradeDTO dto = gradeMapper.toDto(savedGrade);

            // Füge Note auch zum Katalog hinzu
            catalogService.addGradeEntry(student, session.getSubject(), gradeValue);

            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Speichern der Note.");
        }
    }

    /**
     * Überprüft, ob ein Schüler zu einer Unterrichtsstunde gehört.
     * <p>
     * Private Validierungsmethode, die überprüft, ob der Schüler in der
     * Klasse und dem Fach eingeschrieben ist, für das die Stunde abgehalten wird.
     * Die Validierung erfolgt durch Überprüfung des Stundenplans der Schülerklasse.
     *
     * @param student Zu überprüfender Schüler
     * @param session Unterrichtsstunde
     * @return true wenn Schüler zur Stunde gehört, false sonst
     */
    private boolean isStudentInSession(Student student, ClassSession session) {
        return student.getStudentClass().getSchedules().stream()
                .anyMatch(schedule -> schedule.getSubjects().contains(session.getSubject()) &&
                        schedule.getTeacher().getId().equals(session.getTeacher().getId()));
    }

    /**
     * Ruft die Liste der berechtigten Schüler für eine Unterrichtsstunde ab.
     * <p>
     * Zugänglich für Lehrer und Administratoren.
     * Gibt alle Schüler zurück, die an der angegebenen Stunde teilnehmen,
     * basierend auf Stundenplan und Lehrer-Fach-Klassen-Zuordnung.
     * <p>
     * Auswahlprozess:
     * - Identifiziert Klassen, in denen der Lehrer das Fach der Stunde unterrichtet
     * - Sammelt alle Schüler aus diesen Klassen
     * - Überprüft Gültigkeit der Zuordnungen durch Stundenplan
     *
     * @param sessionId ID der Stunde, für die Schüler gesucht werden
     * @return ResponseEntity mit der Liste berechtigter Schüler oder 404 falls Stunde nicht existiert
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/session/{sessionId}/students")
    public ResponseEntity<List<StudentDTO>> getStudentsForSession(@PathVariable Long sessionId) {
        try {
            ClassSession session = classSessionService.getSessionById(sessionId);
            if (session == null) {
                System.out.println("Sitzung nicht gefunden: " + sessionId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            System.out.println("Sitzung gefunden: " + sessionId + ", Fach: " + session.getSubject());

            // Debug-Informationen für Lehrer
            Teacher teacher = session.getTeacher();
            System.out.println("Sitzungslehrer: " + teacher.getName() + ", ID: " + teacher.getId());

            // Verbesserte Herangehensweise: Hole alle Klassen, in denen dieser Lehrer dieses Fach unterrichtet
            List<Class> allClasses = classService.getAllClasses();

            // Filtere Klassen effizienter
            List<Class> relevantClasses = allClasses.stream()
                    .filter(cls -> {
                        if (cls.getSchedules() == null || cls.getSchedules().isEmpty()) {
                            return false;
                        }

                        // Überprüfe, ob ein Stundenplan unseren Kriterien entspricht
                        return cls.getSchedules().stream()
                                .anyMatch(schedule -> {
                                    // Überprüfung: Fächer-Sammlung ist nicht null
                                    boolean hasSubject = schedule.getSubjects() != null &&
                                            schedule.getSubjects().contains(session.getSubject());

                                    // Überprüfung: Ordnungsgemäßer Vergleich der Lehrer-IDs
                                    boolean hasTeacher = schedule.getTeacher() != null &&
                                            schedule.getTeacher().getId().equals(teacher.getId());

                                    return hasSubject && hasTeacher;
                                });
                    })
                    .collect(Collectors.toList());

            System.out.println("Relevante Klassen gefunden: " + relevantClasses.size());

            // Falls keine relevanten Klassen gefunden werden, protokolliere diese Information
            if (relevantClasses.isEmpty()) {
                System.out.println("Warnung: Keine Klassen gefunden, in denen Lehrer " +
                        teacher.getName() + " das Fach " + session.getSubject() + " unterrichtet");
            } else {
                for (Class cls : relevantClasses) {
                    System.out.println("Relevante Klasse: " + cls.getName() + " (ID: " + cls.getId() + ")");
                }
            }

            // Hole Schüler effizienter durch direkte Filterung nach Klassen-IDs
            List<Long> relevantClassIds = relevantClasses.stream()
                    .map(Class::getId)
                    .collect(Collectors.toList());

            // Hole alle Schüler
            List<Student> allStudents = studentService.getAllStudents();

            // Keine relevanten Klassen bedeutet keine Schüler
            if (relevantClassIds.isEmpty()) {
                System.out.println("Keine relevanten Klassen gefunden, leere Schülerliste wird zurückgegeben");
                return ResponseEntity.ok(new ArrayList<>());
            }

            // Filtere Schüler nach Klassen-ID
            List<Student> studentsInRelevantClasses = allStudents.stream()
                    .filter(student -> {
                        if (student.getStudentClass() == null) {
                            return false;
                        }

                        return relevantClassIds.contains(student.getStudentClass().getId());
                    })
                    .collect(Collectors.toList());

            System.out.println(studentsInRelevantClasses.size() + " Schüler in relevanten Klassen gefunden");

            // Konvertiere zu DTOs
            List<StudentDTO> studentDTOs = studentsInRelevantClasses.stream()
                    .map(studentMapper::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(studentDTOs);
        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen der Schüler für Sitzung: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}