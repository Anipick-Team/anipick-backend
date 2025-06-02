package com.anipick.backend.user.component;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.user.domain.LoginFormat;
import com.anipick.backend.user.domain.UserDefaults;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class NicknameInitializer {
    private static final int NICKNAME_THRESHOLD = 10;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final RedisTemplate<String, String> redisTemplate;

    public String generateNickname(LoginFormat loginFormat) {
        String redisKey = UserDefaults.DEFAULT_NICKNAME_FORMAT_KEY + loginFormat;
        Long count = redisTemplate.opsForValue().increment(redisKey);

        if(count == null) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if(count >= NICKNAME_THRESHOLD) {
            redisTemplate.delete(redisKey);
        }

        long normalizedCount = count % NICKNAME_THRESHOLD;
        String timeStamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String nickname = loginFormat + timeStamp + normalizedCount;

        return nickname;
    }
}
