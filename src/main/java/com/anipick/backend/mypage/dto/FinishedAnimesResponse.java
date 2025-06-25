package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FinishedAnimesResponse {
    private Long count;
    private CursorDto cursor;
    private List<FinishedAnimesDto> animes;

    public static FinishedAnimesResponse from(Long count, CursorDto cursor, List<FinishedAnimesDto> animes) {
        return new FinishedAnimesResponse(count, cursor, animes);
    }
}
