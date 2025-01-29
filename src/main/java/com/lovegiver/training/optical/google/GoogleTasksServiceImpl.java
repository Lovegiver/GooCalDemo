package com.lovegiver.training.optical.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class GoogleTasksServiceImpl implements GoogleTasksService<Tasks> {

    private final GoogleOAuthService oAuthService;

    private static final Logger LOG = Logger.getLogger(GoogleTasksServiceImpl.class);

    private final Map<String, Tasks> usersTasks = new HashMap<>();

    private static final String APPLICATION_NAME = "OptimeApp";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Inject
    public GoogleTasksServiceImpl(GoogleOAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Override
    public Tasks getService(String userUUID) throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return usersTasks.getOrDefault(userUUID, new Tasks
                .Builder(HTTP_TRANSPORT, JSON_FACTORY, this.oAuthService.getCredentials(HTTP_TRANSPORT, userUUID))
                .setApplicationName(APPLICATION_NAME)
                .build());
    }

    @Override
    public void getUserTasks(String userUUID) throws IOException, GeneralSecurityException, URISyntaxException {
        LOG.warn("Not implemented yet");
    }
}
