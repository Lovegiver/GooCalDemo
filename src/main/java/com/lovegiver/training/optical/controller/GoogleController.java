package com.lovegiver.training.optical.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovegiver.training.optical.payload.Credentials;
import com.lovegiver.training.optical.service.CalendarService;
import com.lovegiver.training.optical.service.UserService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicReference;

@Path("/googlecalendar")
@PermitAll
public class GoogleController {

    private final CalendarService calendarService;
    private final UserService userService;

    @Inject
    public GoogleController(CalendarService calendarService, UserService userService) {
        this.calendarService = calendarService;
        this.userService = userService;
    }

    @POST
    @Path(("/user"))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signup(String json) throws JsonProcessingException {
        AtomicReference<Response> response = new AtomicReference<>();
        Credentials credentials = new ObjectMapper().readValue(json, Credentials.class);
        System.out.println("Credentials " + credentials);
        String uuid = this.userService.addUser(credentials);
        System.out.println(uuid);
        response.set(Response.ok(uuid).build());
        return response.get();
    }

    @Path("/Callback")
    @POST
    public Response callback(String code) {
        System.out.println("Received code: " + code);
        return Response.ok().build();
    }

}
