package com.anipick.backend.user.domain;

import lombok.Getter;

@Getter
public enum UserAnimeOfStatus {
    /**
     * 볼 애니
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
