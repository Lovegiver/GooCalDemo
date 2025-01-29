package com.lovegiver.training.optical.google;

import com.lovegiver.training.optical.payload.Message;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

public interface GoogleCalendarService<SERVICE> extends GoogleGenericService<SERVICE> {

    void getUserEvents(String userUUID) throws IOException, GeneralSecurityException, URISyntaxException;

    Message<?> subscribeEvents(String userUUID) throws IOException, GeneralSecurityException, URISyntaxException;

    Message<?> unsubscribeEvents(String userUUID, String calendarId, long createdAt) throws IOException, GeneralSecurityException, URISyntaxException;

}
