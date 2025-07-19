package com.anipick.backend.recommendation.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRecommendState {
    private Long userId;
    private UserRecommendMode mode;
    private Long referenceAnimeId;
    private LocalDateTime startDate;

    public static UserRecommendState createRecommendState(Long userId, Long referenceAnimeId) {
        return new UserRecommendState(userId, UserRecommendMode.RECENT_HIGH, referenceAnimeId, LocalDateTime.now());
    }
}
