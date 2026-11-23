package com.anipick.backend.anime.dto;

import lombok.Getter;

/**
 * 애니 하나의 시청 상태별 유저 수 (탈퇴 유저 제외)
 */
@Getter
public class AnimeSocialCountDto {
    private long watchlistCount;
    private long watchingCount;
    private long finishedCount;
}
