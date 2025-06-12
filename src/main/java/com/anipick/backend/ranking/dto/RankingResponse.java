package com.anipick.backend.ranking.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RankingResponse {
    private CursorDto cursorDto;
    private List<RankingAnimesDto> rankingAnimesDtos;


}
