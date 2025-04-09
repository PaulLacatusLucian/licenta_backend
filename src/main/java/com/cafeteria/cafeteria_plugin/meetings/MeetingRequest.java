package com.cafeteria.cafeteria_plugin.meetings;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;


import java.time.ZonedDateTime;
import java.util.List;

@Data
public class MeetingRequest {
    private String className;

    private List<String> parentEmails;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime startDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime endDateTime;

}
