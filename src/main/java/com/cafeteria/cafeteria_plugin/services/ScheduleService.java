package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Schedule;
import com.cafeteria.cafeteria_plugin.repositories.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    // Ein Stundenplan wird hinzugefügt
    public Schedule addSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    // Alle Stundenpläne abrufen
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // Einen bestimmten Stundenplan nach ID abrufen
    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    // Einen vorhandenen Stundenplan aktualisieren
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

    // Einen Stundenplan löschen
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    // Stundenpläne nach Klassen-ID abrufen (inkl. Lehrerinformationen)
    public List<Schedule> getSchedulesByClassId(Long classId) {
        return scheduleRepository.findAllByClassIdWithTeacher(classId);
    }

    // Stundenpläne nach Klassenname abrufen (inkl. Lehrerinformationen)
    public List<Schedule> getSchedulesByClassName(String className) {
        return scheduleRepository.findAllByClassNameWithTeacher(className);
    }
}