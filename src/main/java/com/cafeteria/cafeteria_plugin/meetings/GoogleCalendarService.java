package com.cafeteria.cafeteria_plugin.meetings;

import com.cafeteria.cafeteria_plugin.meetings.GoogleCalendarProperties;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GoogleCalendarService {

    private final GoogleCalendarProperties props;
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public GoogleCalendarService(GoogleCalendarProperties props) {
        this.props = props;
    }

    public String getAccessToken() throws IOException {
        TokenResponse tokenResponse = new GoogleRefreshTokenRequest(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                props.getRefreshToken(),
                props.getClientId(),
                props.getClientSecret()
        ).execute();

        return tokenResponse.getAccessToken();
    }

    public String createMeeting(String summary, ZonedDateTime start, ZonedDateTime end, List<String> attendeeEmails) throws IOException {
        String accessToken = getAccessToken();

        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory((HttpRequest request) -> {
            request.setParser(new JsonObjectParser(JSON_FACTORY));
            request.getHeaders().setAuthorization("Bearer " + accessToken);
        });

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String formattedStart = formatter.format(start);
        String formattedEnd = formatter.format(end);


        Map<String, Object> eventData = new HashMap<>();
        eventData.put("summary", summary);
        eventData.put("description", "Elternbesprechung");

        Map<String, String> startObj = Map.of(
                "dateTime", formattedStart,
                "timeZone", "Europe/Bucharest"
        );

        Map<String, String> endObj = Map.of(
                "dateTime", formattedEnd,
                "timeZone", "Europe/Bucharest"
        );

        eventData.put("start", startObj);
        eventData.put("end", endObj);

        List<Map<String, String>> attendees = new ArrayList<>();
        for (String email : attendeeEmails) {
            attendees.add(Map.of("email", email));
        }
        eventData.put("attendees", attendees);

        Map<String, Object> conferenceData = new HashMap<>();
        Map<String, String> createRequest = new HashMap<>();
        createRequest.put("requestId", UUID.randomUUID().toString());
        conferenceData.put("createRequest", createRequest);
        eventData.put("conferenceData", conferenceData);

        GenericUrl url = new GenericUrl("https://www.googleapis.com/calendar/v3/calendars/primary/events?conferenceDataVersion=1&sendUpdates=all");
        HttpContent content = new ByteArrayContent("application/json", JSON_FACTORY.toByteArray(eventData));

        System.out.println(JSON_FACTORY.toPrettyString(eventData));


        HttpRequest request = requestFactory.buildPostRequest(url, content);
        HttpResponse response = request.execute();

        @SuppressWarnings("unchecked")
        Map<String, Object> json = response.parseAs(Map.class);

        Map<String, Object> conference = (Map<String, Object>) json.get("conferenceData");
        Map<String, Object> entryPoint = ((List<Map<String, Object>>) conference.get("entryPoints")).get(0);
        return (String) entryPoint.get("uri");
    }
}
