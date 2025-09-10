package com.anipick.backend.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class HomeRecentReviewAnimeAllTitleItemDto {
    private Long reviewId;
    private Long userId;
    private Long animeId;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String reviewContent;
    private String nickname;
    private String createdAt;
}
