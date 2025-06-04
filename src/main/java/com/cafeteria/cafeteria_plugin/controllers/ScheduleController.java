package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.ScheduleDTO;
import com.cafeteria.cafeteria_plugin.mappers.ScheduleMapper;
import com.cafeteria.cafeteria_plugin.models.Schedule;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.cafeteria.cafeteria_plugin.models.ClassSession;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.*;

/**
 * REST-Controller für die Verwaltung von Stundenplänen und Unterrichtsplänen.
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für die umfassende Verwaltung von
 * Stundenplänen bereit und ermöglicht die Erstellung, Bearbeitung und
 * Abfrage von Unterrichtsplänen für verschiedene Klassen und Bildungsebenen.
 * Sie unterstützt sowohl die administrative Stundenplanverwaltung als auch
 * die personalisierte Stundenplaneinsicht für Schüler.
 * <p>
 * Hauptfunktionen:
 * - CRUD-Operationen für Stundenplaneinträge
 * - Bildungsebenen-spezifische Lehrervalidierung
 * - Automatische Unterrichtsstunden-Erstellung bei Stundenplanerstellung
 * - Tages- und wöchentliche Stundenplanabfragen
 * - JWT-basierte personalisierte Stundenplaneinsicht für Schüler
 * - Zeitvalidierung und Konfliktprüfung
 * - Mehrsprachige Tagesunterstützung (Englisch-Rumänisch-Übersetzung)
 * <p>
 * Bildungsebenen-Regeln:
 * - Grundschule (PRIMARY): Erfordert Erzieher als Klassenlehrer
 * - Mittel-/Oberschule: Erfordert reguläre Fachlehrer
 * - Automatische Lehrerzuordnung basierend auf Bildungsebene
 * - Validierung der Lehrer-Klassen-Zuordnung
 * <p>
 * Sicherheit:
 * - Rollenbasierte Zugriffskontrolle für verschiedene Operationen
 * - Lehrer und Administratoren können Stundenpläne verwalten
 * - Schüler können nur ihre eigenen Stundenpläne einsehen
 * - JWT-Authentifizierung für personalisierte Abfragen
 * - Sichere Datenvalidierung bei Erstellung und Aktualisierung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see ScheduleService
 * @see Schedule
 * @see ClassSession
 * @see Teacher
 * @see Student
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    /**
     * Service für Stundenplan-Operationen und -Verwaltung.
     */
    @Autowired
    private ScheduleService scheduleService;

    /**
     * Service für Unterrichtsstunden-Operationen.
     */
    @Autowired
    private ClassSessionService classSessionService;

    /**
     * Mapper für Transformation von Schedule-Entitäten in DTOs.
     */
    @Autowired
    private ScheduleMapper scheduleMapper;

    /**
     * Service für Lehreroperationen und -validierung.
     */
    @Autowired
    private TeacherService teacherService;

    /**
     * Service für Schüleroperationen.
     */
    @Autowired
    private StudentService studentService;

    /**
     * Hilfsprogramm für JWT-Token-Verwaltung.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Service für Klassenoperationen und -validierung.
     */
    @Autowired
    private ClassService classService;

    /**
     * Fügt einen neuen Stundenplaneintrag hinzu mit automatischer Unterrichtsstunden-Erstellung.
     * <p>
     * Nur für Lehrer und Administratoren zugänglich.
     * Erstellt einen neuen Stundenplaneintrag mit umfassender Validierung
     * der Bildungsebenen-Regeln und automatischer Zuordnung entsprechender Lehrer.
     * Generiert automatisch eine korrespondierende Unterrichtsstunde für
     * die spätere Anwesenheits- und Notenverwaltung.
     * <p>
     * Bildungsebenen-Logik:
     * - PRIMARY (Grundschule): Automatische Zuordnung des Klassenerziehers
     * - MIDDLE/HIGH: Manuelle Lehrerauswahl erforderlich
     * - Validierung der Lehrer-Typ-Kompatibilität
     * <p>
     * Zeitvalidierung:
     * - Parsing von Start- und Endzeiten im Format "H:mm"
     * - Validierung dass Endzeit nach Startzeit liegt
     * - Automatische ClassSession-Erstellung mit korrekten Zeitstempeln
     *
     * @param schedule Stundenplan-Objekt mit allen erforderlichen Daten
     * @return ResponseEntity mit erstelltem Stundenplan oder Fehler bei ungültigen Daten
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ScheduleDTO> addSchedule(@RequestBody Schedule schedule) {
        try {
            if (schedule.getStudentClass() == null || schedule.getStudentClass().getId() == null) {
                return ResponseEntity.badRequest().build();
            }

            com.cafeteria.cafeteria_plugin.models.Class fullClass =
                    classService.getClassById(schedule.getStudentClass().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Klasse wurde nicht gefunden"));

            schedule.setStudentClass(fullClass);

            Teacher fullTeacher;

            if (fullClass.getEducationLevel() == com.cafeteria.cafeteria_plugin.models.EducationLevel.PRIMARY) {
                fullTeacher = teacherService.getEducatorByClassId(fullClass.getId());
                if (fullTeacher == null) {
                    return ResponseEntity.badRequest().body(null);
                }
                schedule.setTeacher(fullTeacher);
            } else {
                if (schedule.getTeacher() == null || schedule.getTeacher().getId() == null) {
                    return ResponseEntity.badRequest().build();
                }
                fullTeacher = teacherService.getTeacherById(schedule.getTeacher().getId());
                schedule.setTeacher(fullTeacher);
            }

            if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
                return ResponseEntity.badRequest().build();
            }

            LocalTime startTime = LocalTime.parse(schedule.getStartTime(), DateTimeFormatter.ofPattern("H:mm"));
            LocalTime endTime = LocalTime.parse(schedule.getEndTime(), DateTimeFormatter.ofPattern("H:mm"));

            if (!endTime.isAfter(startTime)) {
                return ResponseEntity.badRequest().build();
            }

            Schedule savedSchedule = scheduleService.addSchedule(schedule);

            ClassSession classSession = new ClassSession();
            classSession.setSubject(schedule.getSubjects().get(0));
            classSession.setTeacher(fullTeacher);
            classSession.setStartTime(LocalDateTime.of(LocalDate.now(), startTime));
            classSession.setEndTime(LocalDateTime.of(LocalDate.now(), endTime));
            classSession.setScheduleDay(schedule.getScheduleDay());
            classSession.setClassName(schedule.getStudentClass().getName());
            classSessionService.addClassSession(classSession);

            return ResponseEntity.ok(scheduleMapper.toDto(savedSchedule));

        } catch (Exception e) {
            System.out.println("Fehler bei der Stundenplan-Verarbeitung: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Ruft alle Stundenpläne im System ab.
     * <p>
     * Öffentlich zugängliche Methode für administrative Übersichten.
     * Gibt eine vollständige Liste aller registrierten Stundenpläne
     * zurück, einschließlich aller Klassen, Lehrer und Fächer.
     * Nützlich für systemweite Stundenplananalysen und Berichte.
     *
     * @return ResponseEntity mit der Liste aller Stundenpläne im System
     */
    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        List<ScheduleDTO> dtos = scheduleService.getAllSchedules().stream()
                .map(scheduleMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Ruft einen spezifischen Stundenplan anhand seiner ID ab.
     * <p>
     * Ermöglicht den Abruf detaillierter Informationen zu einem
     * einzelnen Stundenplaneintrag einschließlich aller zugeordneten
     * Lehrer, Fächer und Zeitinformationen.
     *
     * @param id Eindeutige ID des Stundenplans
     * @return ResponseEntity mit Stundenplan-Details oder 404 falls nicht gefunden
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id)
                .map(scheduleMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Aktualisiert einen existierenden Stundenplan.
     * <p>
     * Nur für Lehrer und Administratoren zugänglich.
     * Ermöglicht die Änderung aller Stundenplan-Attribute einschließlich
     * Zeiten, Lehrer, Fächer und Klassenzuordnungen.
     * Führt die gleichen Validierungen durch wie bei der Erstellung.
     *
     * @param id       ID des zu aktualisierenden Stundenplans
     * @param schedule Stundenplan-Objekt mit neuen Daten
     * @return ResponseEntity mit aktualisiertem Stundenplan
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long id, @RequestBody Schedule schedule) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, schedule));
    }

    /**
     * Löscht einen Stundenplan vollständig aus dem System.
     * <p>
     * Nur für Administratoren zugänglich.
     * Entfernt den Stundenplan und alle zugehörigen Referenzen
     * aus dem System. Kann Auswirkungen auf verknüpfte
     * Unterrichtsstunden haben.
     *
     * @param id ID des zu löschenden Stundenplans
     * @return ResponseEntity mit No-Content-Status bei Erfolg
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ruft alle Stundenpläne für eine spezifische Klasse ab.
     * <p>
     * Gibt den kompletten Wochenstundenplan für eine bestimmte
     * Klasse zurück, einschließlich aller Fächer, Lehrer und Zeiten.
     * Nützlich für Klassenübersichten und Stundenplan-Darstellungen.
     *
     * @param classId ID der Klasse, deren Stundenplan abgerufen werden soll
     * @return ResponseEntity mit der Liste aller Stundenpläne der Klasse
     */
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByClassId(@PathVariable Long classId) {
        List<ScheduleDTO> dtos = scheduleService.getSchedulesByClassId(classId).stream()
                .map(scheduleMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Ruft den heutigen Stundenplan für eine spezifische Klasse ab.
     * <p>
     * Filtert die Stundenpläne einer Klasse nach dem aktuellen Tag
     * und gibt nur die heute stattfindenden Unterrichtsstunden zurück.
     * Verwendet automatische Tagesbestimmung und Mehrsprachen-Übersetzung
     * von Englisch zu Rumänisch für Tagesbezeichnungen.
     *
     * @param classId ID der Klasse, deren heutiger Stundenplan abgerufen werden soll
     * @return ResponseEntity mit der Liste der heutigen Unterrichtsstunden
     */
    @GetMapping("/class/{classId}/today")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesForToday(@PathVariable Long classId) {
        String englishCurrentDay = LocalDate.now()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String romanianCurrentDay = DAY_TRANSLATION.getOrDefault(englishCurrentDay, "");

        List<Schedule> schedulesForToday = scheduleService.getSchedulesByClassId(classId).stream()
                .filter(schedule -> schedule.getScheduleDay().equalsIgnoreCase(romanianCurrentDay))
                .toList();

        List<ScheduleDTO> dtos = schedulesForToday.stream()
                .map(scheduleMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    /**
     * Ruft den morgigen Stundenplan für eine spezifische Klasse ab.
     * <p>
     * Filtert die Stundenpläne einer Klasse nach dem morgigen Tag
     * und gibt nur die morgen stattfindenden Unterrichtsstunden zurück.
     * Verwendet Klassennamen anstatt ID für flexiblere Abfragen
     * und automatische Tagesberechnung mit Sprach-Übersetzung.
     *
     * @param className Name der Klasse, deren morgiger Stundenplan abgerufen werden soll
     * @return ResponseEntity mit der Liste der morgigen Unterrichtsstunden
     */
    @GetMapping("/class/{className}/tomorrow")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesForTomorrow(@PathVariable String className) {
        String englishNextDay = LocalDate.now()
                .plusDays(1)
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String romanianNextDay = DAY_TRANSLATION.getOrDefault(englishNextDay, "");
        className = className.toUpperCase();

        List<Schedule> schedulesForNextDay = scheduleService.getSchedulesByClassName(className).stream()
                .filter(schedule -> schedule.getScheduleDay().equalsIgnoreCase(romanianNextDay))
                .toList();

        List<ScheduleDTO> dtos = schedulesForNextDay.stream()
                .map(scheduleMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    /**
     * Ruft den wöchentlichen Stundenplan für den angemeldeten Schüler ab.
     * <p>
     * Nur für Schüler mit STUDENT-Rolle zugänglich.
     * Ermöglicht es Schülern, ihren kompletten Wochenstundenplan
     * einzusehen. Der Schüler wird über den JWT-Token identifiziert
     * und der Stundenplan seiner Klasse automatisch abgerufen.
     *
     * @param token JWT-Authentifizierungs-Token des Schülers
     * @return ResponseEntity mit dem wöchentlichen Stundenplan oder 404 falls Schüler nicht gefunden
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me/weekly")
    public ResponseEntity<List<ScheduleDTO>> getWeeklyScheduleForStudent(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Student student = studentService.findByUsername(username);

        if (student == null || student.getStudentClass() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<ScheduleDTO> schedule = scheduleService
                .getSchedulesByClassId(student.getStudentClass().getId())
                .stream()
                .map(scheduleMapper::toDto)
                .toList();

        return ResponseEntity.ok(schedule);
    }

    /**
     * Ruft den heutigen Stundenplan für den angemeldeten Schüler ab.
     * <p>
     * Nur für Schüler mit STUDENT-Rolle zugänglich.
     * Ermöglicht es Schülern, ihren heutigen Stundenplan einzusehen.
     * Der Schüler wird über den JWT-Token identifiziert und nur
     * die heute stattfindenden Unterrichtsstunden seiner Klasse
     * werden zurückgegeben.
     * <p>
     * Funktionalität:
     * - Automatische Schüleridentifikation über JWT
     * - Tagesberechnung und Sprachübersetzung
     * - Filterung nach aktuellem Tag
     * - Personalisierte Stundenplaneinsicht
     *
     * @param token JWT-Authentifizierungs-Token des Schülers
     * @return ResponseEntity mit dem heutigen Stundenplan oder 404 falls Schüler nicht gefunden
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me/today")
    public ResponseEntity<List<ScheduleDTO>> getTodayScheduleForStudent(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Student student = studentService.findByUsername(username);

        if (student == null || student.getStudentClass() == null) {
            return ResponseEntity.notFound().build();
        }

        String today = LocalDate.now()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String romanianToday = DAY_TRANSLATION.getOrDefault(today, "");

        List<ScheduleDTO> schedule = scheduleService
                .getSchedulesByClassId(student.getStudentClass().getId())
                .stream()
                .filter(s -> s.getScheduleDay().equalsIgnoreCase(romanianToday))
                .map(scheduleMapper::toDto)
                .toList();

        return ResponseEntity.ok(schedule);
    }

    /**
     * Übersetzungstabelle für Tagesbezeichnungen von Englisch zu Rumänisch.
     * <p>
     * Statische Zuordnung zur Konvertierung der Java-Tagesbezeichnungen
     * (die in englischer Sprache geliefert werden) zu den im System
     * verwendeten rumänischen Tagesbezeichnungen. Wird für die
     * tagesbasierten Stundenplanfilterungen verwendet.
     */
    private static final Map<String, String> DAY_TRANSLATION = new HashMap<>() {{
        put("Monday", "Luni");
        put("Tuesday", "Marți");
        put("Wednesday", "Miercuri");
        put("Thursday", "Joi");
        put("Friday", "Vineri");
        put("Saturday", "Sâmbătă");
        put("Sunday", "Duminică");
    }};
}