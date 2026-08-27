package com.anipick.backend.mypage.dto;

import lombok.Getter;

/**
 * 내가 작성한 게시글 조회 원본. 시리즈 제목 다국어 픽은 MyCommunityPostDto 에서 수행.
 * animeTitle/animeCoverImageUrl 은 게시글이 속한 시리즈(게시판)의 제목/커버를 사용한다
 * (시리즈와 대표작 애니의 제목/커버가 데이터상 동일).
 */
@Getter
public class MyCommunityPostAllTitleDto {
    private Long postId;
    private Long seriesId;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;
    private String title;
    private String content;
    private Long thumbnailImageId;
    private Boolean isSpoiler;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String createdAt;
}
