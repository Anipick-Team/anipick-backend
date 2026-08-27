package com.anipick.backend.community.domain;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 게시글 목록 정렬 옵션.
 * - latest: 최신순 (post_id DESC)
 * - popularDaily/Weekly/Monthly: 해당 기간 내 증가한 좋아요 수 기준 인기순
 *   (community_post_like.created_at 이 기간 시작 이후인 것만 카운트)
 */
@Getter
@AllArgsConstructor
public enum CommunityPostSort {
    LATEST("latest"),
    POPULAR_DAILY("popularDaily"),
    POPULAR_WEEKLY("popularWeekly"),
    POPULAR_MONTHLY("popularMonthly");

    private final String code;

    public static CommunityPostSort of(String code) {
        if (code == null) {
            return LATEST;
        }
        return switch (code) {
            case "latest" -> LATEST;
            case "popularDaily" -> POPULAR_DAILY;
            case "popularWeekly" -> POPULAR_WEEKLY;
            case "popularMonthly" -> POPULAR_MONTHLY;
            default -> throw new CustomException(ErrorCode.BAD_REQUEST);
        };
    }

    public boolean isPopular() {
        return this != LATEST;
    }

    /**
     * 인기순 집계 시작 시각. 최신순이면 null.
     */
    public LocalDateTime periodStart(LocalDateTime now) {
        return switch (this) {
            case POPULAR_DAILY -> now.minusDays(1);
            case POPULAR_WEEKLY -> now.minusWeeks(1);
            case POPULAR_MONTHLY -> now.minusMonths(1);
            case LATEST -> null;
        };
    }
}