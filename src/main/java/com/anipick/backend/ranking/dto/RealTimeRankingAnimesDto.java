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
    private Long change;
    private String trend;
    private List<String> genres;

    public static RealTimeRankingAnimesDto from(Long animeId, Long rank, Long change, String trend, RealTimeRankingAnimesFromQueryDto dto) {
        return new RealTimeRankingAnimesDto(
                animeId,
                dto.getTitle(),
                dto.getCoverImageUrl(),
                rank,
                change,
                trend,
                dto.getGenres().stream()
                        .map(GenreDto::getName)
                        .limit(3)
                        .toList()
        );
    }
}
