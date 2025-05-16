package com.anipick.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RecentReviewItemDto {
    private Long reviewId;
    private Long animeId;
    private String animeTitle;
    private String animeCoverImageUrl;
    private Double rating;
    private String reviewContent;
    private String nickname;
    private String profileImageUrl;
    private String createdAt;
    private Long likeCount;
    private Boolean likedByCurrentUser;
    private Boolean isMine;
}
