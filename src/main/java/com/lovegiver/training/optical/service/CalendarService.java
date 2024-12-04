package com.lovegiver.training.optical.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;

public interface CalendarService {

    void getGoogleCalendarEvents(String uuid) throws IOException, GeneralSecurityException, ExecutionException, InterruptedException;

}
