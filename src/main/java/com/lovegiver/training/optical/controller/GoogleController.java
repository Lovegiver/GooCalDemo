package com.lovegiver.training.optical.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovegiver.training.optical.payload.Credentials;
import com.lovegiver.training.optical.payload.EventsWatchContent;
import com.lovegiver.training.optical.payload.Message;
import com.lovegiver.training.optical.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestHeader;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
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

    @GET
    @Path("/callback")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response callback() {
        LOG.debug("Callback function");
        return Response.accepted().build();
    }

    @POST
    @Path("/notifications")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response watch(
            @RestHeader("X-Goog-Channel-ID") String channelId,
            @RestHeader("X-Goog-Channel-Token") String channelToken,
            @RestHeader("X-Goog-Channel-Expiration") String channelExpiration,
            @RestHeader("X-Goog-Resource-ID") String resourceId,
            @RestHeader("X-Goog-Resource-URI") String resourceUri,
            @RestHeader("X-Goog-Resource-State") String resourceState,
            @RestHeader("X-Goog-Message-Number") long messageNumber
    ) throws JsonProcessingException {
        LOG.info("WATCH function");
        if (channelId != null) {
            LOG.info("channelId received: " + channelId);
        } else {
            LOG.info("channelId received null");
        }
        if (channelToken != null) {
            LOG.info("channelToken received: " + channelToken);
        } else {
            LOG.info("channelToken received null");
        }
        if (channelExpiration != null) {
            LOG.info("channelExpiration received: " + channelExpiration);
        } else {
            LOG.info("channelExpiration received null");
        }
        if (resourceId != null) {
            LOG.info("resourceId received: " + resourceId);
        } else {
            LOG.info("resourceId received null");
        }
        if (resourceUri != null) {
            LOG.info("resourceUri received: " + resourceUri);
        } else {
            LOG.info("resourceUri received null");
        }
        if (resourceState != null) {
            LOG.info("resourceState received: " + resourceState);
        } else {
            LOG.info("resourceState received null");
        }
        if (messageNumber != 0) {
            LOG.info("messageNumber received: " + messageNumber);
        } else {
            LOG.info("messageNumber received = 0");
        }
        return Response.accepted().build();
    }

}
