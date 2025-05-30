package com.anipick.backend.oauth.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoDefaults {
    public static final String DEFAULT_REST_CLIENT_BASE_URL = "https://kapi.kakao.com";
    public static final String DEFAULT_POST_URI = "/v2/user/me";
    public static final String DEFAULT_ACCOUNT_ROOT_PATH = "kakao_account";
    public static final String DEFAULT_EMAIL_PATH = "email";
}
