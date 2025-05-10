package com.anipick.backend.anime.service.dto;

import com.anipick.backend.anime.domain.Season;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeasonDto {
    private final int id;
    private final String name;

    public static SeasonDto from(Season season) {
        return new SeasonDto(season.getCode(), season.getName());
    }
}
