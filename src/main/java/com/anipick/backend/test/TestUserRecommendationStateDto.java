package com.anipick.backend.test;

import com.anipick.backend.recommendation.domain.UserRecommendMode;
import lombok.Getter;

@Getter
public class TestUserRecommendationStateDto {
    private Long userId;
    private UserRecommendMode mode;
}
