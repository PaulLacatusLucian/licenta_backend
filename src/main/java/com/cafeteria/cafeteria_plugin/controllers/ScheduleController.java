package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Schedule;
import com.cafeteria.cafeteria_plugin.services.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.services.ClassSessionService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.*;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ClassSessionService classSessionService;

    public ScheduleController(ScheduleService scheduleService, ClassSessionService classSessionService) {
        this.scheduleService = scheduleService;
        this.classSessionService = classSessionService;
    }


    @PostMapping
    public ResponseEntity<Schedule> addSchedule(@RequestBody Schedule schedule) {
        try {
            // Log the received data
            System.out.println("Received schedule: " + schedule);

            // Validări de bază pentru Schedule
            if (schedule.getStudentClass() == null || schedule.getStudentClass().getId() == null) {
                return ResponseEntity.badRequest().body(null);
            }

            if (schedule.getTeacher() == null || schedule.getTeacher().getId() == null) {
                return ResponseEntity.badRequest().body(null);
            }

            if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
                return ResponseEntity.badRequest().body(null);
            }

            // Parse the start and end times using LocalTime
            try {
                LocalTime startTime = LocalTime.parse(schedule.getStartTime(), DateTimeFormatter.ofPattern("H:mm"));
                LocalTime endTime = LocalTime.parse(schedule.getEndTime(), DateTimeFormatter.ofPattern("H:mm"));

                // Validate logic (e.g., end time should be after start time)
                if (!endTime.isAfter(startTime)) {
                    return ResponseEntity.badRequest().body(null);
                }
            } catch (DateTimeParseException e) {
                System.out.println("Time parsing error: " + e.getMessage());
                return ResponseEntity.badRequest().body(null);
            }

            // Salvează Schedule
            Schedule savedSchedule = scheduleService.addSchedule(schedule);

            // Creează un ClassSession asociat
            ClassSession classSession = new ClassSession();
            classSession.setSubject(schedule.getSubjects().get(0)); // Presupunem că există cel puțin un subiect
            classSession.setTeacher(schedule.getTeacher());
            classSession.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.parse(schedule.getStartTime(), DateTimeFormatter.ofPattern("H:mm"))));
            classSession.setEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.parse(schedule.getEndTime(), DateTimeFormatter.ofPattern("H:mm"))));

            classSessionService.addClassSession(classSession); // Serviciul responsabil pentru ClassSession

            return ResponseEntity.ok(savedSchedule);
        } catch (Exception e) {
            System.out.println("Error processing schedule: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }



    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);  // Wrap the List in ResponseEntity
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Long id) {
        Optional<Schedule> schedule = scheduleService.getScheduleById(id);
        return schedule.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long id, @RequestBody Schedule schedule) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, schedule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Schedule>> getSchedulesByClassId(@PathVariable Long classId) {
        List<Schedule> schedules = scheduleService.getSchedulesByClassId(classId);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/class/{classId}/today")
    public ResponseEntity<List<Schedule>> getSchedulesForToday(@PathVariable String className) {
        String englishCurrentDay = LocalDate.now()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String romanianCurrentDay = DAY_TRANSLATION.getOrDefault(englishCurrentDay, "");

        List<Schedule> schedulesForToday = scheduleService.getSchedulesByClassName(className).stream()
                .filter(schedule -> schedule.getScheduleDay().equalsIgnoreCase(romanianCurrentDay))
                .toList();

        return ResponseEntity.ok(schedulesForToday);
    }


    @GetMapping("/class/{className}/tomorrow")
    public ResponseEntity<List<Schedule>> getSchedulesForTomorrow(@PathVariable String className) {
        // Obține ziua următoare în limba engleză
        String englishNextDay = LocalDate.now()
                .plusDays(1)
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        // Traduce ziua în română
        String romanianNextDay = DAY_TRANSLATION.getOrDefault(englishNextDay, "");
        className = className.toUpperCase();

        // Filtrare orare pentru ziua următoare
        List<Schedule> schedulesForNextDay = scheduleService.getSchedulesByClassName(className).stream()
                .filter(schedule -> schedule.getScheduleDay().equalsIgnoreCase(romanianNextDay))
                .toList();

        return ResponseEntity.ok(schedulesForNextDay);
    }


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
