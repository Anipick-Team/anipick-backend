package com.anipick.backend.community.domain;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 커뮤니티 탐색 게시판 정렬 옵션.
 * - popular: 실시간 랭킹 기준(소속 애니의 trending, popularity) 시리즈 순
 * - latest: 가장 최근에 게시글이 올라온 시리즈 순 (게시글 없는 시리즈는 맨 뒤)
 */
@Getter
@AllArgsConstructor
public enum CommunityBoardSort {
    POPULAR("popular"),
    LATEST("latest");

    private final String code;

    public static CommunityBoardSort of(String code) {
        if (code == null) {
            return POPULAR;
        }
        return switch (code) {
            case "popular" -> POPULAR;
            case "latest" -> LATEST;
            default -> throw new CustomException(ErrorCode.BAD_REQUEST);
        };
    }
}
