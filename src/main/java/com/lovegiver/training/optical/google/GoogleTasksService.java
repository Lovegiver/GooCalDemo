package com.lovegiver.training.optical.google;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

public interface GoogleTasksService<SERVICE> extends GoogleGenericService<SERVICE> {

    void getUserTasks(String userUUID) throws IOException, GeneralSecurityException, URISyntaxException;

}
