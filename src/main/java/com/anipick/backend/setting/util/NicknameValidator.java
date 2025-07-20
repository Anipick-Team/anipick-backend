package com.anipick.backend.setting.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NicknameValidator {
    private static final Pattern NICKNAME_PATTERN
            = Pattern.compile("^[a-zA-Z0-9가-힣!@#$%^&*()_\\-+=\\[\\]{}|\\\\:;'\",.<>?/]+$");

    public static boolean checkValidateNickname(String nickname) {
        if(nickname == null || nickname.isEmpty() || nickname.length() > 20) {
            return false;
        }

        return NICKNAME_PATTERN.matcher(nickname).matches();
    }
}
