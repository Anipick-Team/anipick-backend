package com.anipick.backend.auth.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailDefaults {
    public static final String EMAIL_VERIFICATION_SUBJECT = "이메일 인증";
    public static final String EMAIL_VERIFICATION_NUMBER_MESSAGE = "<h2>요청하신 인증 번호는</h2>";
    public static final String H1_NUMBER_FORMAT = "<h1>%s</h1>";
    public static final String LAST_MESSAGE = "<h3>애니픽을 이용해주셔서 감사합니다.</h3>";
    public static final String SENDER_EMAIL_KEY = "senderEmail:";
}
