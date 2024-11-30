package com.lovegiver.training.optical.controller;

import com.lovegiver.training.optical.service.CalendarService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Path("/googlecalendar")
public class GoogleController {

    private CalendarService calendarService;

    @Inject
    public GoogleController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @Path("/")
    @PermitAll
    @GET
    public void launch() throws GeneralSecurityException, IOException {
        this.calendarService.getGoogleCalendarEvents();
    }

    @Path("/Callback")
    @PermitAll
    @POST
    public Response callback(String code) {
        System.out.println("Received code: " + code);
        return Response.ok().build();
    }

}
