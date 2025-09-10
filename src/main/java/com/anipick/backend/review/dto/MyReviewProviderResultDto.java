package com.anipick.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyReviewProviderResultDto {
    private Long reviewId;
    private Long animeId;
    private Double rating;
    private String content;
    private Boolean isSpoiler;

    public static MyReviewProviderResultDto nonContent(MyReviewProviderResultDto dto) {
        return new MyReviewProviderResultDto(
                dto.getReviewId(),
                dto.getAnimeId(),
                dto.getRating(),
                null,
                dto.getIsSpoiler()
        );
    }

    public static MyReviewProviderResultDto of(MyReviewProviderResultDto dto) {
        return new MyReviewProviderResultDto(
                dto.getReviewId(),
                dto.getAnimeId(),
                dto.getRating(),
                dto.getContent(),
                dto.getIsSpoiler()
        );
    }
}
