package com.anipick.backend.community.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 커뮤니티 메인 피드 게시글 원본. 시리즈 제목 선택과 날짜 포맷은 서비스에서 처리.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedPostRawDto {
    private Long postId;
    private Long seriesId;
    private String seriesTitleMan;
    private String seriesTitleKor;
    private String seriesTitleEng;
    private String seriesTitleRom;
    private String seriesTitleNat;
    private Long userId;
    private String nickname;
    private Long profileImageId;
    private String title;
    private String content;
    private Long thumbnailImageId;
    private Long imageCount;
    private Boolean isSpoiler;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String createdAt;
    /**
     * 실시간 인기 피드에서만 채워지는 최근 7일 좋아요 수(커서 lastValue).
     */
    private Long periodLikeCount;
}
