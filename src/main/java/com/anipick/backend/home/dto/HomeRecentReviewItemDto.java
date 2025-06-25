package com.anipick.backend.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class HomeRecentReviewItemDto {
    private Long reviewId;
    private Long animeId;
    private String animeTitle;
    private String reviewContent;
    private String nickname;
    private String createdAt;
}
