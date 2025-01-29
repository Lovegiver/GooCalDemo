package com.lovegiver.training.optical.service;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.tasks.Tasks;
import com.lovegiver.training.optical.entity.User;
import com.lovegiver.training.optical.google.GoogleCalendarService;
import com.lovegiver.training.optical.google.GoogleTasksService;
import com.lovegiver.training.optical.payload.Credentials;
import com.lovegiver.training.optical.payload.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SignServiceImpl implements SignService {

    private final UserService userService;
    private final GoogleCalendarService<Calendar> googleCalendarService;
    private final GoogleTasksService<Tasks> googleTasksService;

    private static final Logger LOG = Logger.getLogger(SignServiceImpl.class);

    @Inject
    public SignServiceImpl(
            UserService userService,
            GoogleCalendarService<Calendar> googleCalendarService,
            GoogleTasksService<Tasks> googleTasksService
    ) {
        this.userService = userService;
        this.googleCalendarService = googleCalendarService;
        this.googleTasksService = googleTasksService;
    }

    @Override
    public Message<?> signin(Credentials credentials) throws GeneralSecurityException, IOException, URISyntaxException {
        Message<?> message;
        UUID uuid;
        Optional<User> optionalUser = this.userService.findByUsernameAndPassword(credentials);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            uuid = user.uniqueId;
            LOG.info("User found -----> " + uuid.toString());
            message = this.googleCalendarService.subscribeEvents(uuid.toString());
        } else {
            message = new Message<>("Username or password is incorrect.", true);
        }
        return message;
    }

    @Override
    public Message<?> signup(Credentials credentials) throws GeneralSecurityException, IOException, URISyntaxException {
        Message<?> message;
        Optional<User> optionalUser = this.userService.findByUsernameAndPassword(credentials);
        if (optionalUser.isPresent()) {
            LOG.error("----- User already exists -----");
            message = new Message<>("This username already exists.", true);
        } else {
            message = this.userService.addUser(credentials);
            LOG.info("User created -----> " + message.payload().toString());
            message = this.googleCalendarService.subscribeEvents(message.payload().toString());
        }
        return message;
    }
}
