package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class WatchListAnimesResponse {
    private Long count;
    private CursorDto cursor;
    private List<WatchListAnimesDto> animes;

    public static WatchListAnimesResponse from(Long count, CursorDto cursor, List<WatchListAnimesDto> animes) {
        return new WatchListAnimesResponse(count, cursor, animes);
    }
}
