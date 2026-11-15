package com.anipick.backend.community.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 오늘의 방영작 라운지 아이템(시리즈 = 게시판 단위).
 * episode 는 Redis schedule:week:current 의 회차 값이며, 같은 시리즈의 애니가 여러 개면 인기 순위가 가장 높은 애니의 값.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoungeSeriesItemDto {
    private Long seriesId;
    private String title;
    private String coverImageUrl;
    private Integer episode;
}
