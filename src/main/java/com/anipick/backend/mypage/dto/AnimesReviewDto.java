package com.anipick.backend.mypage.dto;

import lombok.Getter;

@Getter
public class AnimesReviewDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private Double rating;
    private Long reviewId;
    private String reviewContent;
    private String createdAt;
    private Long likeCount;
    private Boolean isLiked;
}
