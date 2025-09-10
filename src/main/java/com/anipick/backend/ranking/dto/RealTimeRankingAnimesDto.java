package com.anipick.backend.ranking.dto;

import com.anipick.backend.anime.dto.GenreDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RealTimeRankingAnimesDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private Long rank;
    private String change;
    private String trend;
    private List<String> genres;
    private Long popularity;
    private Long trending;

    public static RealTimeRankingAnimesDto from(Long rank, String change, String trend, RealTimeRankingAnimesFromQueryDto dto, List<String> genres) {
        return new RealTimeRankingAnimesDto(
                dto.getAnimeId(),
                dto.getTitle(),
                dto.getCoverImageUrl(),
                rank,
                change,
                trend,
                genres.stream()
                        .limit(3)
                        .toList(),
                dto.getPopularity(),
                dto.getTrending()
        );
    }
}
