package com.anipick.backend.anime.dto;

import com.anipick.backend.search.dto.StudioItemDto;
import com.anipick.backend.user.domain.UserAnimeOfStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    // 랭킹 제외 대상(방영 미정, 성인, 장르 없음)이면 null -> 응답에서 제외
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AnimeDetailYearSeasonRankingDto yearSeasonRanking;
    private String status;
    private String age;
    private List<StudioItemDto> studios;
}
