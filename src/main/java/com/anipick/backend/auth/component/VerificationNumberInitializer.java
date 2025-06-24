package com.anipick.backend.auth.component;

import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
public class VerificationNumberInitializer {
    public String initVerificationNumber() throws NoSuchAlgorithmException {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
