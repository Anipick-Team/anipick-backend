package com.anipick.backend.anime.domain;

/**
 * 애니 상세 소셜 지표의 노출 단계
 */
public enum AnimeSocialPhase {
    /**
     * 방영 예정
     */
    NOT_YET_RELEASED,
    /**
     * 방영 시작 직후 (시작일 포함 7일)
     */
    SEASON_START,
    /**
     * 방영 중
     */
    RELEASING,
    /**
     * 방영 종료
     */
    FINISHED
}
