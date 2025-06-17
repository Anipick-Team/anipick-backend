package com.anipick.backend.user.domain;

import lombok.Getter;

@Getter
public enum UserWatchStatus {
    /**
     * 볼 예정
     */
    WATCHLIST,
    /**
     * 보는 중
     */
    WATCHING,
    /**
     * 다 본 애니
     */
    FINISHED
}
