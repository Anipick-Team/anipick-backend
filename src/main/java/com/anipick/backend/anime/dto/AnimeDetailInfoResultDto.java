package com.anipick.backend.anime.dto;

import com.anipick.backend.search.dto.StudioItemDto;
import com.anipick.backend.user.domain.UserAnimeOfStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimeDetailInfoResultDto {
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
    private List<GenreDto> genres;
    private Long episode;
    private String airDate;
    private String status;
    private String age;
    private List<StudioItemDto> studios;
}
