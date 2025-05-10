package com.anipick.backend.anime.controller.dto;

import com.anipick.backend.anime.domain.AnimeFormat;
import com.anipick.backend.anime.service.dto.GenreDto;
import com.anipick.backend.anime.service.dto.SeasonDto;

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
