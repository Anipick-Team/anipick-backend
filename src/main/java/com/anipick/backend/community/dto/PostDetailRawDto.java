package com.anipick.backend.community.dto;

import lombok.Getter;

/**
 * 게시글 상세 조회 원본(이미지 목록 제외). 날짜 포맷/플래그는 서비스에서 가공.
 */
@Getter
public class PostDetailRawDto {
    private Long postId;
    private Long seriesId;
    private String seriesTitleKor;
    private String seriesTitleEng;
    private String seriesTitleRom;
    private String seriesTitleNat;
    private Long userId;
    private String nickname;
    private Long profileImageId;
    private String title;
    private String content;
    private Boolean isSpoiler;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Boolean isLiked;
    private Boolean isAuthorBlocked;
    private String createdAt;
}
