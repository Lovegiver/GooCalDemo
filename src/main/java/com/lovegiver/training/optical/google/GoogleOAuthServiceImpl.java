package com.lovegiver.training.optical.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.lovegiver.training.optical.service.CalendarServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class GoogleOAuthServiceImpl implements GoogleOAuthService {

    private final DataStoreFactory dbDataStoreFactory;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final Logger LOG = Logger.getLogger(GoogleOAuthServiceImpl.class);

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    @Inject
    public GoogleOAuthServiceImpl(DataStoreFactory dbDataStoreFactory) {
        this.dbDataStoreFactory = dbDataStoreFactory;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @param userUUID The {@link com.lovegiver.training.optical.entity.User} unique ID
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Transactional
    public Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String userUUID)
            throws IOException {
        // Load client secrets.
        InputStream in = CalendarServiceImpl.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setDataStoreFactory(this.dbDataStoreFactory)
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8888)
                .setCallbackPath("/optime/googlecalendar/Callback")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize(userUUID);
        //returns an authorized Credential object.
        LOG.debug("Access token: " + credential.getAccessToken());
        LOG.debug("Refresh token: " + credential.getRefreshToken());
        LOG.debug("Expiry: " + credential.getExpirationTimeMilliseconds());
        return credential;
    }
}
