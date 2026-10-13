package com.anipick.backend.anime.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AnimeDayOfTheWeekPageDto {
    private Long count;
    private CursorDto cursor;
    private List<AnimeItemDto> animes;

    public static AnimeDayOfTheWeekPageDto empty() {
        return new AnimeDayOfTheWeekPageDto(0L, null, List.of());
    }
}
