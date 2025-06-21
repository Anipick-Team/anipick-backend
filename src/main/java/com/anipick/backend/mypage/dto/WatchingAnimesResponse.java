package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.Getter;

import java.util.List;

@Getter
public class WatchingAnimesResponse {
    private Long count;
    private CursorDto cursor;
    private List<WatchingAnimesDto> animes;
}
