package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.ScheduleDTO;
import com.cafeteria.cafeteria_plugin.mappers.ScheduleMapper;
import com.cafeteria.cafeteria_plugin.models.Schedule;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import com.cafeteria.cafeteria_plugin.services.ClassService;
import com.cafeteria.cafeteria_plugin.services.ScheduleService;
import com.cafeteria.cafeteria_plugin.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ClassSessionService classSessionService;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ClassService classService;



    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ScheduleDTO> addSchedule(@RequestBody Schedule schedule) {
        try {
            if (schedule.getStudentClass() == null || schedule.getStudentClass().getId() == null) {
                return ResponseEntity.badRequest().build();
            }

            com.cafeteria.cafeteria_plugin.models.Class fullClass =
                    classService.getClassById(schedule.getStudentClass().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Clasa nu a fost găsită"));

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

            // Creează și sesiunea de clasă
            ClassSession classSession = new ClassSession();
            classSession.setSubject(schedule.getSubjects().get(0));
            classSession.setTeacher(fullTeacher);
            classSession.setStartTime(LocalDateTime.of(LocalDate.now(), startTime));
            classSession.setEndTime(LocalDateTime.of(LocalDate.now(), endTime));
            classSessionService.addClassSession(classSession);

            return ResponseEntity.ok(scheduleMapper.toDto(savedSchedule));

        } catch (Exception e) {
            System.out.println("Error processing schedule: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }



    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        List<ScheduleDTO> dtos = scheduleService.getAllSchedules().stream()
                .map(scheduleMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id)
                .map(scheduleMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long id, @RequestBody Schedule schedule) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, schedule));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByClassId(@PathVariable Long classId) {
        List<ScheduleDTO> dtos = scheduleService.getSchedulesByClassId(classId).stream()
                .map(scheduleMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }


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



    @GetMapping("/class/{className}/tomorrow")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesForTomorrow(@PathVariable String className) {
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

        List<ScheduleDTO> dtos = schedulesForNextDay.stream()
                .map(scheduleMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
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
