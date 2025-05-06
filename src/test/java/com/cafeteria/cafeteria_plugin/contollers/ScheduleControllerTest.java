//package com.cafeteria.cafeteria_plugin.contollers;
//
//import com.cafeteria.cafeteria_plugin.controllers.ScheduleController;
//import com.cafeteria.cafeteria_plugin.mappers.ScheduleMapper;
//import com.cafeteria.cafeteria_plugin.models.Schedule;
//import com.cafeteria.cafeteria_plugin.services.ScheduleService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ScheduleControllerTest {
//
//    @Mock
//    private ScheduleService scheduleService;
//
//    @InjectMocks
//    private ScheduleController scheduleController;
//
//    @Mock
//    private ScheduleMapper scheduleMapper;
//
//
//    private Schedule schedule;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // Creează un obiect Schedule cu LocalDateTime
//        schedule = new Schedule();
//        schedule.setId(1L);
//        schedule.setScheduleDay("Monday");
//        schedule.setStartTime(String.valueOf(LocalDateTime.of(2025, 1, 19, 8, 0))); // 2025-01-19T08:00
//        schedule.setEndTime(String.valueOf(LocalDateTime.of(2025, 1, 19, 10, 0))); // 2025-01-19T10:00
//    }
//
//    @Test
//    public void testAddSchedule() {
//        when(scheduleService.addSchedule(any(Schedule.class))).thenReturn(schedule);
//
//        ResponseEntity<Schedule> response = scheduleController.addSchedule(schedule);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(schedule, response.getBody());
//    }
//
//    @Test
//    public void testGetAllSchedules() {
//        when(scheduleService.getAllSchedules()).thenReturn(List.of(schedule));
//
//        ResponseEntity<List<Schedule>> response = scheduleController.getAllSchedules();
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue(response.getBody().contains(schedule));
//    }
//
//    @Test
//    public void testGetScheduleById() {
//        when(scheduleService.getScheduleById(1L)).thenReturn(Optional.of(schedule));
//
//        ResponseEntity<Schedule> response = scheduleController.getScheduleById(1L);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(schedule, response.getBody());
//    }
//
//    @Test
//    public void testGetScheduleByIdNotFound() {
//        when(scheduleService.getScheduleById(1L)).thenReturn(Optional.empty());
//
//        ResponseEntity<Schedule> response = scheduleController.getScheduleById(1L);
//        assertEquals(404, response.getStatusCodeValue());
//    }
//
//    @Test
//    public void testUpdateSchedule() {
//        Schedule updatedSchedule = new Schedule();
//        updatedSchedule.setScheduleDay("Tuesday");
//        updatedSchedule.setStartTime(String.valueOf(LocalDateTime.of(2025, 1, 19, 10, 0))); // 2025-01-19T10:00
//        updatedSchedule.setEndTime(String.valueOf(LocalDateTime.of(2025, 1, 19, 12, 0))); // 2025-01-19T12:00
//
//        when(scheduleService.updateSchedule(1L, updatedSchedule)).thenReturn(updatedSchedule);
//
//        ResponseEntity<Schedule> response = scheduleController.updateSchedule(1L, updatedSchedule);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(updatedSchedule, response.getBody());
//    }
//
//    @Test
//    public void testDeleteSchedule() {
//        doNothing().when(scheduleService).deleteSchedule(1L);
//
//        ResponseEntity<Void> response = scheduleController.deleteSchedule(1L);
//        assertEquals(204, response.getStatusCodeValue());
//    }
//}
