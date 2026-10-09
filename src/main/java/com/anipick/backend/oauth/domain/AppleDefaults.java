package com.anipick.backend.oauth.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppleDefaults {
    public static final String DEFAULT_ISSUER = "https://appleid.apple.com";
    public static final String DEFAULT_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";
    public static final String DEFAULT_EMAIL_CLAIM = "email";
}
