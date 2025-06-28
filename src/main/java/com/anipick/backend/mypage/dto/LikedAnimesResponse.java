package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LikedAnimesResponse {
    private Long count;
    private CursorDto cursor;
    private List<LikedAnimesDto> animes;

    public static LikedAnimesResponse from(Long count, CursorDto cursorDto, List<LikedAnimesDto> animes) {
        return new LikedAnimesResponse(count, cursorDto, animes);
    }
}
