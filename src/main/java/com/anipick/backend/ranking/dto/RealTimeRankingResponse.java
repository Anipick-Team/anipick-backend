package com.anipick.backend.ranking.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RealTimeRankingResponse {
    private CursorDto cursor;
    private List<RealTimeRankingAnimesDto> animes;

    public static RealTimeRankingResponse of(CursorDto cursor, List<RealTimeRankingAnimesDto> animes) {
        return new RealTimeRankingResponse(cursor, animes);
    }
}
