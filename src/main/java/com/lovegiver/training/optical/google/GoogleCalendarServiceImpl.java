package com.lovegiver.training.optical.google;

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
import com.lovegiver.training.optical.entity.SyncToken;
import com.lovegiver.training.optical.payload.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class GoogleCalendarServiceImpl implements GoogleCalendarService<Calendar> {

    private final GoogleOAuthService oAuthService;

    private final Map<String, Calendar> usersCalendars = new HashMap<>();

    private static final String APPLICATION_NAME = "OptimeApp";
    private static final String WATCH_NOTIFICATION_URL = "https://lovegiver.net/optime/googlecalendar/notifications";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final Logger LOG = Logger.getLogger(GoogleCalendarServiceImpl.class);

    @Inject
    public GoogleCalendarServiceImpl(GoogleOAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Override
    public Calendar getService(String userUUID) throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return usersCalendars.getOrDefault(userUUID, new Calendar
                .Builder(HTTP_TRANSPORT, JSON_FACTORY, this.oAuthService.getCredentials(HTTP_TRANSPORT, userUUID))
                .setApplicationName(APPLICATION_NAME)
                .build());
    }

    @Override
    public void getUserEvents(String userUUID) throws IOException, GeneralSecurityException {
        LOG.debug("Received UUID: " + userUUID);
        Calendar calendar = this.getService(userUUID);
        this.usersCalendars.put(userUUID, calendar);

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


    }

    @Override
    public Message<?> subscribeEvents(String userUUID) {
        Message<?> message;
        try {
            Calendar calendar = this.getService(userUUID);
            CalendarList calendarList = calendar.calendarList().list().execute();
            calendarList.getItems().forEach( calendarListEntry -> {
                LOG.info("CALENDAR LIST ITEM ID -----> " + calendarListEntry.getId());
                // TODO : c'est ici qu'il faut enregistrer dans la DB tous les Events de l'utilisateur
                this.persistNextSyncToken(calendar, userUUID, calendarListEntry.getId(), calendarList.getNextSyncToken());
            });
            message = new Message<>("User Events are now watched.");
        } catch (Exception e) {
            LOG.error("Error while subscribing events: " + e.getMessage());
            message = new Message<>("Error while subscribing events : " + e.getMessage(), true);
        }
        return message;
    }

    @Override
    public Message<?> unsubscribeEvents(String userUUID, String calendarId, long createdAt) {
        Message<?> message;
        try {
            Calendar calendar = this.getService(userUUID);
            String channelId = String.format("%s_%s", userUUID, createdAt);
            calendar.channels().stop(new Channel().setId(channelId).setType("web_hook")).execute();
            message = new Message<>("User Events unsubscribed.");
        } catch (Exception e) {
            LOG.error("Error while unsubscribing events: " + e.getMessage());
            message = new Message<>("Error while unsubscribing events.", true);
        }
        return message;
    }

    @Transactional(rollbackOn = IOException.class)
    public void persistNextSyncToken(Calendar calendar, String userUUID, String calendarId, String nextSyncToken) {
        LOG.info(String.format("----- WATCH: Subscribing to calendar [ %s ] events for user %s -----", calendarId, userUUID));

        SyncToken validToken = null;
        Optional<List<SyncToken>> optionalSyncTokenList = SyncToken.findByUserUuidAndCalendarId(UUID.fromString(userUUID), calendarId);
        if (optionalSyncTokenList.isPresent() && !optionalSyncTokenList.get().isEmpty()) {
            LOG.info("PERSISTING NEXT SYNC TOKEN -----> TOKEN FOUND !!");
            SyncToken mostRecentSyncToken = optionalSyncTokenList.get().stream()
                    .max(Comparator.comparingLong(token -> token.tokenExpiry))
                    .orElse(null);
            if (mostRecentSyncToken != null && !mostRecentSyncToken.isDisposable()) {
                validToken = mostRecentSyncToken;
                optionalSyncTokenList.get().stream()
                        .filter(syncToken -> syncToken != mostRecentSyncToken)
                        .forEach(syncToken -> this.unsubscribeEvents(userUUID, calendarId, syncToken.createdAt.toInstant().toEpochMilli()));
            } else {
                optionalSyncTokenList.get().forEach(
                        syncToken -> this.unsubscribeEvents(userUUID, calendarId, syncToken.createdAt.toInstant().toEpochMilli())
                );
            }
        } else {
            LOG.info("PERSISTING NEXT SYNC TOKEN -----> NO TOKEN FOUND");
        }

        if (validToken == null) {
            Channel channel;
            try {
                Instant createdAt = Instant.now().atZone(ZoneId.of("UTC")).toInstant();
                long createdTimestamp = createdAt.toEpochMilli();
                LOG.info("----- CREATING CHANNEL -----");
                Calendar.Events.Watch watched = calendar.events().watch(
                        calendarId,
                        new Channel()
                                .setId(String.format("%s_%s", userUUID, createdTimestamp))
                                .setType("web_hook")
                                .setAddress(WATCH_NOTIFICATION_URL)
                                .setToken(Long.toString(createdTimestamp))
                );
                channel = watched.execute(); // IOException may occur here
                LOG.info("----- CHANNEL CREATED -----");

                SyncToken syncToken = SyncToken.Builder.builder()
                        .userUuid(UUID.fromString(userUUID))
                        .calendarId(calendarId)
                        .nextSyncToken(nextSyncToken)
                        .createdAt(new Timestamp(createdTimestamp))
                        .tokenExpiry(channel.getExpiration())
                        .build();
                syncToken.persistAndFlush();
                LOG.info("----- SYNC TOKEN PERSISTED -----");
            } catch (IOException e) {
                LOG.error("IOException while trying to subscribe to the calendar: " + e.getMessage());
                if (e.getMessage().contains("Push notifications are not supported by this resource")) {
                    LOG.warn("This calendar should be added to list of not suitable resources : " + calendarId);
                }
            }
        }
    }

}
