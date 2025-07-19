package com.anipick.backend.anime.domain;

import lombok.Getter;

@Getter
public enum AnimeCharacterRole {
    /**
     * 주연
     */
    MAIN,
    /**
     * 조연
     */
    SUPPORTING,
    /**
     * 배경 캐릭터
     */
    BACKGROUND
}
