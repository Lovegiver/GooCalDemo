package com.lovegiver.training.optical.google;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleGenericService<SERVICE> {

    SERVICE getService(String userUUID) throws GeneralSecurityException, IOException;

}
