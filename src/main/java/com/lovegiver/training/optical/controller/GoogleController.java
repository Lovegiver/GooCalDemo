package com.lovegiver.training.optical.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovegiver.training.optical.payload.Credentials;
import com.lovegiver.training.optical.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.concurrent.atomic.AtomicReference;

@Path("/googlecalendar")
@PermitAll
public class GoogleController {

    private final UserService userService;

    private static final Logger LOG = Logger.getLogger(GoogleController.class);

    @Inject
    public GoogleController(UserService userService) {
        this.userService = userService;
    }

    @POST
    @Path(("/user"))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signup(String json) throws JsonProcessingException {
        AtomicReference<Response> response = new AtomicReference<>();
        Credentials credentials = new ObjectMapper().readValue(json, Credentials.class);
        LOG.debug("Credentials " + credentials);
        String uuid = this.userService.addUser(credentials);
        LOG.debug(uuid);
        response.set(Response.ok(uuid).build());
        return response.get();
    }

    @Path("/Callback")
    @POST
    public Response callback(String code) {
        LOG.debug("Callback function -> Received code: " + code);
        return Response.ok().build();
    }

}
