package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class AnimeDetailInfoReviewsRequestDto {
    private Long animeId;
    private Long userId;
    private String sort;
    private String orderByQuery;
    private Boolean isSpoiler;
    private Long lastId;
    private String lastValue;
    private int size;
}
