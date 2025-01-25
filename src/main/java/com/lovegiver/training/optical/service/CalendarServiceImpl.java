package com.lovegiver.training.optical.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.lovegiver.training.optical.google.GoogleOAuthService;
import com.lovegiver.training.optical.google.GoogleOAuthServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    public void getUserEvents(String uuid) throws IOException, GeneralSecurityException, URISyntaxException {
        LOG.debug("Received UUID: " + uuid);
        Calendar calendar = this.getUserCalendar(uuid);
        this.usersCalendars.put(uuid, calendar);

        // Primary Calendars
        LOG.info("----- PRIMARY CALENDAR -----");
        Calendar.Calendars calendars = calendar.calendars();
        com.google.api.services.calendar.model.Calendar primary = calendars.get("primary").execute();
        LOG.info("PRIMARY ID: " + primary.getId());
        LOG.info("PRIMARY SUMMARY: " + primary.getSummary());
        LOG.info("PRIMARY KIND: " + primary.getKind());
        LOG.info("PRIMARY ETAG: " + primary.getEtag());

        // Calendars list
        LOG.info("----- CALENDARS LIST -----");
        CalendarList list = calendar.calendarList().list().execute();
        LOG.info("CALENDARS LIST ETAG: " + list.getEtag());
        LOG.info("CALENDARS LIST KIND: " + list.getKind());
        LOG.info("CALENDARS LIST NEXT PAGE TOKEN: " + list.getNextPageToken());
        LOG.info("CALENDARS LIST NEXT SYNC TOKEN: " + list.getNextSyncToken());
        LOG.info("CALENDARS LIST SIZE: " + list.getItems().size());
        list.getItems().forEach( item -> {
            LOG.info("ITEM ID: " + item.getId());
            LOG.info("ITEM DESC:" + item.getDescription());
            LOG.info("ITEM KIND:" + item.getKind());
            LOG.info("ITEM SUMMARY:" + item.getSummary());
            LOG.info("ITEM ETAG:" + item.getEtag());
            LOG.info("ITEM PRIMARY: " + item.getPrimary());
        });

        // List the next 10 events from the primary calendar.
        LOG.info("----- EVENTS LIST -----");
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = calendar.events().list("primary")
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

        LOG.info("----- WATCH -----");
        URI uri = new URI("https://lovegiver.net/optime/googlecalendar/notifications");
        Calendar.Events.Watch watched = calendar.events().watch(
                "primary",
                new Channel()
                        .setId(uuid)
                        .setType("web_hook")
                        .setAddress(uri.toURL().toString())
                        .setToken("token-" + uuid)
        );
        Channel channel = watched.execute();
        String id = channel.getId();
        LOG.info("Found events for " + uuid + ": " + watched);

        LOG.info("WATCH -> " + watched.getCalendarId());
        LOG.info("WATCH -> " + watched.getOauthToken());
        LOG.info("WATCH -> " + watched.getUriTemplate());
        LOG.info("WATCH -> " + watched.getHttpContent().getType());
        LOG.info("WATCH -> " + watched.getLastResponseHeaders());
        LOG.info("CHANNEL -> " + channel.getId());
    }

}
