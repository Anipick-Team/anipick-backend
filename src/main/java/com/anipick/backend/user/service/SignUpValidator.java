package com.anipick.backend.user.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SignUpValidator {
    private static final Pattern EMAIL_PATTERN
            = Pattern.compile("^(?!.*\\.\\.)[a-z0-9]+(\\.[a-z0-9]+)*@[a-z0-9]+(\\.[a-z0-9]+)*$");
    private static final Pattern PASSWORD_PATTERN
            = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+$");

    public static boolean checkValidationEmail(String email) {
        if(email == null || email.isEmpty() || email.length() > 50) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean checkValidationPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 16) {
            return false;
        }

        if(!PASSWORD_PATTERN.matcher(password).matches()) {
            return false;
        }

        boolean hasLetter   = password.matches(".*[a-zA-Z].*"); // 대소문자 상관없이
        boolean hasDigit    = password.matches(".*\\d.*");
        boolean hasSpecial  = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        return hasLetter && hasDigit && hasSpecial;
    }
}
