package com.anipick.backend.anime.dto;

import com.anipick.backend.anime.domain.AnimeStatus;
import com.anipick.backend.user.domain.UserAnimeOfStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AnimeDetailInfoItemDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private String bannerImageUrl;
    private String description;
    private String averageRating;
    private Boolean isLiked;
    private UserAnimeOfStatus watchStatus;
    private String type;
    private int reviewCount;
    private Long episode;
    private LocalDate startDate;
    private AnimeStatus status;
    private String age;
}
