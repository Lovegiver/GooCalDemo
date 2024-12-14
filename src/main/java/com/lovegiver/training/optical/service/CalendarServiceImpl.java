package com.lovegiver.training.optical.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.lovegiver.training.optical.google.GoogleOAuthService;
import com.lovegiver.training.optical.google.GoogleOAuthServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CalendarServiceImpl implements CalendarService {

    private final GoogleOAuthService oAuthService;

    private final Map<String, Calendar> usersCalendars = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(GoogleOAuthServiceImpl.class);

    private static final String APPLICATION_NAME = "OptimeApp";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Inject
    public CalendarServiceImpl(GoogleOAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    /**
     * The wanted Calendar object is either already in the Map, or has to be built once and then put in the Map.
     *
     * @param uuid {@link String} representation of the user's UUID
     * @return A {@link Calendar} object
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private Calendar getUserCalendar(String uuid) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return usersCalendars.getOrDefault(uuid, new Calendar
                .Builder(HTTP_TRANSPORT, JSON_FACTORY, this.oAuthService.getCredentials(HTTP_TRANSPORT, uuid))
                .setApplicationName(APPLICATION_NAME)
                .build());
    }

    public void getUserEvents(String uuid) throws IOException, GeneralSecurityException {
        LOG.debug("Received UUID: " + uuid);
        Calendar service = this.getUserCalendar(uuid);
        this.usersCalendars.put(uuid, service);
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            LOG.debug("No upcoming events found.");
        } else {
            LOG.debug("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                LOG.debugf("%s (%s)\n", event.getSummary(), start);
            }
        }
    }

}
