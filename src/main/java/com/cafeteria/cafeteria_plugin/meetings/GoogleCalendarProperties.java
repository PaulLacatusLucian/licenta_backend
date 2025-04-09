package com.cafeteria.cafeteria_plugin.meetings;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "google.calendar")
public class GoogleCalendarProperties {
    private String clientId;
    private String clientSecret;
    private String refreshToken;
}
