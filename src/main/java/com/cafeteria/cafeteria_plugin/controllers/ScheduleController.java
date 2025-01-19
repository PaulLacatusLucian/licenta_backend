package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Schedule;
import com.cafeteria.cafeteria_plugin.services.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ResponseEntity<Schedule> addSchedule(@RequestBody Schedule schedule) {
        return ResponseEntity.ok(scheduleService.addSchedule(schedule));
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
