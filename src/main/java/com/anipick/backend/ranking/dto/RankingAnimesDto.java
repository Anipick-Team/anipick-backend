package com.anipick.backend.ranking.dto;

import com.anipick.backend.anime.dto.GenreDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

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

    public static RankingAnimesDto from(RankingAnimesDto dto) {
        List<String> genreNames = dto.getGenres().stream()
                .map(GenreDto::getName)
                .toList();

        return new RankingAnimesDto(
                dto.getAnimeId(),
                dto.getTitle(),
                dto.getCoverImageUrl(),
                dto.getRank(),
                dto.getChange(),
                dto.getTrend(),
                genreNames
        );
    }
}
