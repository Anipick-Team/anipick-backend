package com.anipick.backend.mypage.dto;

import lombok.Getter;

@Getter
public class AnimesAllTitleReviewDto {
    private Long animeId;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;
    private Double rating;
    private Long reviewId;
    private String reviewContent;
    private String createdAt;
    private Long likeCount;
    private Boolean isLiked;
}
