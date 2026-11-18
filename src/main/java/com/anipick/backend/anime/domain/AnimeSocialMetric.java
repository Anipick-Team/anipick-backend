package com.anipick.backend.anime.domain;

import com.anipick.backend.user.domain.UserAnimeOfStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 애니 상세 소셜 지표의 집계 기준 (집계 대상 시청 상태와 매핑)
 */
@Getter
@AllArgsConstructor
public enum AnimeSocialMetric {
    /**
     * 볼 애니 (WATCHLIST)
     */
    WANT_TO_WATCH(UserAnimeOfStatus.WATCHLIST),
    /**
     * 보는 중 (WATCHING)
     */
    WATCHING(UserAnimeOfStatus.WATCHING),
    /**
     * 다 본 애니 (FINISHED)
     */
    COMPLETED(UserAnimeOfStatus.FINISHED);

    private final UserAnimeOfStatus watchStatus;
}
