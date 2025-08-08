package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class AnimeDetailInfoReviewsItemDto {
    private Long reviewId;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private Double rating;
    private String content;
    private String createdAt;
    private Boolean isSpoiler;
    private Long likeCount;
    private Boolean isLiked;
    private Boolean isMine;
}
