package com.lovegiver.training.optical.service;

import com.lovegiver.training.optical.payload.Credentials;
import com.lovegiver.training.optical.payload.Message;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

public interface SignService {

    Message<?> signin(Credentials credentials) throws GeneralSecurityException, IOException, URISyntaxException;

    Message<?> signup(Credentials credentials) throws GeneralSecurityException, IOException, URISyntaxException;
}
