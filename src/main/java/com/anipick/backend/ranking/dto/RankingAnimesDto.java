package com.anipick.backend.ranking.dto;

import com.anipick.backend.ranking.domain.Trend;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RankingAnimesDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private Long rank;
    private Long change;
    private String trend;
    private List<String> genres;

    public static RankingAnimesDto from(Long change, Trend trend, Long displayRank, RankingAnimesFromQueryDto dto, List<String> genres) {
        return new RankingAnimesDto(
                dto.getAnimeId(),
                dto.getTitle(),
                dto.getCoverImageUrl(),
                displayRank,
                change,
                trend.toString().toLowerCase(),
                genres.stream()
                        .limit(3)
                        .toList()
        );
    }
}
