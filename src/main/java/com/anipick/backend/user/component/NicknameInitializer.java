package com.anipick.backend.user.component;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.user.domain.LoginFormat;
import com.anipick.backend.user.domain.UserDefaults;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NicknameInitializer {
    private static final int NICKNAME_THRESHOLD = 1000;
    private static final String COLON = ":";

    private final RedisTemplate<String, String> redisTemplate;

    public String generateNickname(LoginFormat loginFormat) {
        String randomUuid = UUID.randomUUID().toString().substring(0, 8);
        String loginFormatFirstLetter = loginFormat.toString().substring(0, 1);
        String redisKey = UserDefaults.DEFAULT_NICKNAME_FORMAT_KEY + loginFormat + COLON + randomUuid;
        Long count = redisTemplate.opsForValue().increment(redisKey);

        if(count == null) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        synchronized (this) {
            if(count == 1L) {
                redisTemplate.expire(redisKey, Duration.ofSeconds(10));
            }
        }

        int tailCount = (int) ((count - 1L) % NICKNAME_THRESHOLD);
        String tailNickname = String.format("%03d", tailCount);

        return loginFormatFirstLetter + randomUuid + tailNickname;
    }
}
