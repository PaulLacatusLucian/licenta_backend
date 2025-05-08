//package com.cafeteria.cafeteria_plugin;
//
//import com.cafeteria.cafeteria_plugin.models.ClassSession;
//import com.cafeteria.cafeteria_plugin.models.Schedule;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.util.List;
//
//public class Cronjob {
//    @Scheduled(cron = "0 0 5 * * *") // Rulează în fiecare zi la ora 5 dimineața
//    public void createDailySessions() {
//        // 1. Obține ziua curentă a săptămânii
//        DayOfWeek today = LocalDate.now().getDayOfWeek();
//        String dayName = convertToRomanianDay(today); // "Luni", "Marți" etc.
//
//        // 2. Obține toate programările pentru această zi
//        List<Schedule> schedulesForToday = scheduleService.getByDay(dayName);
//
//        // 3. Pentru fiecare programare, creează o sesiune
//        for (Schedule schedule : schedulesForToday) {
//            ClassSession session = new ClassSession();
//            session.setTeacher(schedule.getTeacher());
//            session.setSubject(schedule.getSubject());
//            session.setStartTime(combineDateTime(LocalDate.now(), schedule.getStartTime()));
//            session.setEndTime(combineDateTime(LocalDate.now(), schedule.getEndTime()));
//            session.setScheduleDay(dayName);
//            session.setClassName(schedule.getClass().getName());
//
//            classSessionService.addClassSession(session);
//        }
//    }
//}
