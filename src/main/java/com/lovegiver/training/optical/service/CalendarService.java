package com.lovegiver.training.optical.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface CalendarService {

    void getGoogleCalendarEvents() throws IOException, GeneralSecurityException;

}
