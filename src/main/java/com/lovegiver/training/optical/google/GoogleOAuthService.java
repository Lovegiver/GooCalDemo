package com.lovegiver.training.optical.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import jakarta.transaction.Transactional;

import java.io.IOException;

public interface GoogleOAuthService {

    @Transactional
    Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String userUUID)
            throws IOException;
}
