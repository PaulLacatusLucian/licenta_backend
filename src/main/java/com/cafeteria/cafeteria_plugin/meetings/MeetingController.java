package com.cafeteria.cafeteria_plugin.meetings;

import com.cafeteria.cafeteria_plugin.meetings.GoogleCalendarService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/meetings")
public class MeetingController {

    private final GoogleCalendarService calendarService;

    public MeetingController(GoogleCalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public String startMeeting(@RequestBody MeetingRequest request, @RequestHeader("Authorization") String token) throws IOException {

        ZonedDateTime start = request.getStartDateTime() != null ? request.getStartDateTime() : ZonedDateTime.now().plusMinutes(5);
        ZonedDateTime end = request.getEndDateTime() != null ? request.getEndDateTime() : start.plusMinutes(30);

        return calendarService.createMeeting(
                "Ședință cu părinții - " + request.getClassName(),
                start,
                end,
                request.getParentEmails()
        );
    }

}
