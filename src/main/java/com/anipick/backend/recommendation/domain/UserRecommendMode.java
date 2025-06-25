package com.anipick.backend.recommendation.domain;

import lombok.Getter;

@Getter
public enum UserRecommendMode {
    /**
     * 3일동안 최근 평가한 애니에 대해
     */
    RECENT_HIGH,
    /**
     * 내가 고평가한 애니들에 대해
     */
    TAG_BASED
}
