package com.lovegiver.training.optical.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovegiver.training.optical.payload.Credentials;
import com.lovegiver.training.optical.payload.Message;
import com.lovegiver.training.optical.service.SignService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicReference;

@Path("/auth")
@PermitAll
public class AuthController {

    private final SignService signService;

    private static final Logger LOG = Logger.getLogger(AuthController.class);

    @Inject
    public AuthController(SignService signService) {
        this.signService = signService;
    }

    @POST
    @Path(("/signup"))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signup(String json) throws IOException, GeneralSecurityException, URISyntaxException {
        AtomicReference<Response> response = new AtomicReference<>();
        Credentials credentials = new ObjectMapper().readValue(json, Credentials.class);
        LOG.debug("CTRL SIGNUP Credentials -----> " + credentials);
        Message<?> result = this.signService.signup(credentials);
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
    public Response signin(String json) throws IOException, GeneralSecurityException, URISyntaxException {
        AtomicReference<Response> response = new AtomicReference<>();
        Credentials credentials = new ObjectMapper().readValue(json, Credentials.class);
        LOG.debug("CTRL SIGNIN Credentials -----> " + credentials);
        Message<?> result = this.signService.signin(credentials);
        if (result.isError()) {
            LOG.error("ERROR WHEN SIGNING IN : " + result.payload());
            response.set(Response.status(Response.Status.BAD_REQUEST).entity(result.payload()).build());
        } else {
            LOG.info("USER AUTHENTICATED : " + result.payload());
            response.set(Response.accepted(result.payload()).build());
        }
        return response.get();
    }



}
