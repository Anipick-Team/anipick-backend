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
    private List<String> genres;
    private Long popularity;

    public static RankingAnimesDto from(Long displayRank, RankingAnimesFromQueryDto dto, List<String> genres) {
        return new RankingAnimesDto(
                dto.getAnimeId(),
                dto.getTitle(),
                dto.getCoverImageUrl(),
                displayRank,
                genres.stream()
                        .limit(3)
                        .toList(),
                dto.getPopularity()
        );
    }
}
