package com.anipick.backend.anime.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ComingSoonPageDto {
    private Long count;
    private CursorDto cursor;
    private List<ComingSoonItemDto> animes;

    public static ComingSoonPageDto of(Long count, CursorDto cursor, List<ComingSoonItemDto> animes) {
        return new ComingSoonPageDto(count, cursor, animes);
    }
}
