package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Schedule;
import com.cafeteria.cafeteria_plugin.repositories.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Zentraler Service für die Stundenplanverwaltung im Schulverwaltungssystem.
 *
 * Diese Klasse ist verantwortlich für:
 * - Erstellung und Verwaltung von Stundenplänen
 * - Abruf von Stundenplänen nach verschiedenen Kriterien
 * - Verwaltung der zeitlichen Strukturen des Unterrichts
 * - Zuordnung von Lehrern, Fächern und Klassen zu Zeitslots
 *
 * Der Service stellt sicher, dass alle Stundenpläne korrekt verwaltet
 * und die schulischen Zeitstrukturen eingehalten werden.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Schedule
 * @since 2025-01-01
 */
@Service
public class ScheduleService {

    /**
     * Repository für Stundenplan-Operationen.
     */
    private final ScheduleRepository scheduleRepository;

    /**
     * Konstruktor für ScheduleService.
     *
     * @param scheduleRepository Repository für Datenbankoperationen mit Stundenplänen
     */
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Ein Stundenplan wird hinzugefügt.
     *
     * Diese Methode speichert einen neuen Stundenplan in der Datenbank
     * mit allen zugehörigen Informationen wie Zeiten, Fächer und Lehrer.
     *
     * @param schedule Der zu erstellende Stundenplan
     * @return Der gespeicherte Stundenplan mit generierter ID
     */
    public Schedule addSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    /**
     * Alle Stundenpläne abrufen.
     *
     * @return Liste aller Stundenpläne im System
     */
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    /**
     * Einen bestimmten Stundenplan nach ID abrufen.
     *
     * @param id Die ID des gesuchten Stundenplans
     * @return Optional mit dem gefundenen Stundenplan oder leer falls nicht gefunden
     */
    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    /**
     * Einen vorhandenen Stundenplan aktualisieren.
     *
     * Diese Methode sucht den existierenden Stundenplan und aktualisiert
     * alle relevanten Felder mit den neuen Werten.
     *
     * @param id Die ID des zu aktualisierenden Stundenplans
     * @param updatedSchedule Der Stundenplan mit den neuen Daten
     * @return Der aktualisierte Stundenplan
     * @throws IllegalArgumentException Falls kein Stundenplan mit der ID gefunden wird
     */
    public Schedule updateSchedule(Long id, Schedule updatedSchedule) {
        return scheduleRepository.findById(id)
                .map(existingSchedule -> {
                    existingSchedule.setScheduleDay(updatedSchedule.getScheduleDay());
                    existingSchedule.setStartTime(updatedSchedule.getStartTime());
                    existingSchedule.setEndTime(updatedSchedule.getEndTime());
                    existingSchedule.setSubjects(updatedSchedule.getSubjects());
                    return scheduleRepository.save(existingSchedule);
                }).orElseThrow(() -> new IllegalArgumentException("Stundenplan wurde nicht gefunden."));
    }

    /**
     * Einen Stundenplan löschen.
     *
     * @param id Die ID des zu löschenden Stundenplans
     */
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    /**
     * Stundenpläne nach Klassen-ID abrufen (inkl. Lehrerinformationen).
     *
     * Diese Methode ruft alle Stundenpläne für eine bestimmte Klasse ab
     * und lädt dabei auch die zugehörigen Lehrerinformationen.
     *
     * @param classId Die ID der Klasse
     * @return Liste der Stundenpläne für die angegebene Klasse
     */
    public List<Schedule> getSchedulesByClassId(Long classId) {
        return scheduleRepository.findAllByClassIdWithTeacher(classId);
    }

    /**
     * Stundenpläne nach Klassenname abrufen (inkl. Lehrerinformationen).
     *
     * Diese Methode ruft alle Stundenpläne für eine bestimmte Klasse
     * anhand des Klassennamens ab und lädt dabei auch die zugehörigen
     * Lehrerinformationen.
     *
     * @param className Der Name der Klasse
     * @return Liste der Stundenpläne für die angegebene Klasse
     */
    public List<Schedule> getSchedulesByClassName(String className) {
        return scheduleRepository.findAllByClassNameWithTeacher(className);
    }
}