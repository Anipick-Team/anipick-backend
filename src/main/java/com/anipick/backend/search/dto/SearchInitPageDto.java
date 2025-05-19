package com.anipick.backend.search.dto;

import com.anipick.backend.anime.dto.AnimeItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SearchInitPageDto {
    private List<AnimeItemDto> popularAnimes;
}
