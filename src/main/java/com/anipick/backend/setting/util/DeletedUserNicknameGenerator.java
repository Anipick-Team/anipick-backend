package com.anipick.backend.setting.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeletedUserNicknameGenerator {
    private static final String DELETED_SUFFIX_NICKNAME = "탈퇴한 사용자_";

    public static String generateDeletedUserNickname() {
        return DELETED_SUFFIX_NICKNAME + UUID.randomUUID().toString().substring(0, 8);
    }
}
