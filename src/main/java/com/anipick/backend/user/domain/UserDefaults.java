package com.anipick.backend.user.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserDefaults {
    public static final String DEFAULT_PROFILE_IMAGE_URL = "default.png";
    public static final Boolean DEFAULT_ADULT_YN = false;
    public static final Boolean DEFAULT_REVIEW_COMPLETED_YN = false;
    public static final Integer DEFAULT_REFRESH_TOKEN_EXPIRATION_DAYS = 14;
}
