package com.anipick.backend.anime.dto;

import com.anipick.backend.anime.domain.AnimeFormat;
import com.anipick.backend.anime.domain.Season;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MetaDataGroupResult {
    private List<Integer> seasonYear;
    private List<Integer> season;
    private List<GenreDto> genres;
    private List<AnimeFormat> type;
}
