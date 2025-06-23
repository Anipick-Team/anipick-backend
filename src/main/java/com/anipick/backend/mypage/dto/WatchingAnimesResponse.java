package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class WatchingAnimesResponse {
    private Long count;
    private CursorDto cursor;
    private List<WatchingAnimesDto> animes;

    public static WatchingAnimesResponse from(Long count, CursorDto cursor, List<WatchingAnimesDto> animes) {
        return new WatchingAnimesResponse(count, cursor, animes);
    }
}
