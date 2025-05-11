package com.anipick.backend.anime.dto;

import com.anipick.backend.anime.domain.AnimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MetaDataGroupResultDto {
    private List<Integer> seasonYear;
    private List<SeasonDto> season;
    private List<GenreDto> genres;
    private List<AnimeFormat> type;
}
