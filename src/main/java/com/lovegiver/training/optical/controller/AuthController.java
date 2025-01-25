package com.lovegiver.training.optical.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovegiver.training.optical.entity.User;
import com.lovegiver.training.optical.payload.Credentials;
import com.lovegiver.training.optical.payload.Message;
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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Path("/auth")
@PermitAll
public class AuthController {

    private final UserService userService;

    private static final Logger LOG = Logger.getLogger(AuthController.class);

    @Inject
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @POST
    @Path(("/signup"))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signup(String json) throws JsonProcessingException {
        AtomicReference<Response> response = new AtomicReference<>();
        Credentials credentials = new ObjectMapper().readValue(json, Credentials.class);
        LOG.debug("Credentials " + credentials);
        Message<String> result = this.userService.addUser(credentials);
        if (result.isError()) {
            LOG.error("ERROR WHEN ADDING USER : " + result.payload());
            response.set(Response.status(Response.Status.BAD_REQUEST).entity(result.payload()).build());
        } else {
            LOG.info("USER ADDED : " + result.payload());
            response.set(Response.accepted(result.payload()).build());
        }
        return response.get();
    }

    @POST
    @Path(("/signin"))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signin(String json) throws JsonProcessingException {
        AtomicReference<Response> response = new AtomicReference<>();
        Credentials credentials = new ObjectMapper().readValue(json, Credentials.class);
        LOG.debug("Credentials " + credentials);
        Optional<User> optionalUser = this.userService.findByUsernameAndPassword(credentials);
        if (optionalUser.isPresent()) {
            LOG.info("USER FOUND : " + optionalUser.get().uniqueId);
            response.set(Response.ok(optionalUser.get().uniqueId).build());
        } else {
            LOG.error("USER NOT FOUND");
            response.set(Response.status(Response.Status.NOT_FOUND).build());
        }
        return response.get();
    }



}
