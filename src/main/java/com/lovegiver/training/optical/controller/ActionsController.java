package com.lovegiver.training.optical.controller;

import com.lovegiver.training.optical.service.CalendarService;
import com.lovegiver.training.optical.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestHeader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

@Path("/actions")
@RolesAllowed("user")
public class ActionsController {

    private final CalendarService calendarService;
    private final UserService userService;

    private static final Logger LOG = Logger.getLogger(GoogleController.class);

    @Inject
    public ActionsController(CalendarService calendarService, UserService userService) {
        this.calendarService = calendarService;
        this.userService = userService;
    }

    @Path("/")
    @GET
    public void launch(@RestHeader("Authorization") String header) throws GeneralSecurityException, IOException, ExecutionException, InterruptedException {
        LOG.debug("Header = " + header);
        var authHeader = header.substring("Basic".length()).trim();
        var decoded = new String(Base64.getDecoder().decode(authHeader), StandardCharsets.UTF_8);
        LOG.debug("Decoded = " + decoded);
        var split = decoded.split(":");
        var username = split[0];
        var password = split[1];
        LOG.debug("Username: " + username + " Password: " + password);
        this.calendarService.getUserEvents(
                this.userService.findByUsername(username).uniqueId.toString()
        );
    }
}
