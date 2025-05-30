package com.anipick.backend.user.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserAnimeStatus {
    private Long userAnimeStatusId;
    private Long userId;
    private Long animeId;
    private UserAnimeOfStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
