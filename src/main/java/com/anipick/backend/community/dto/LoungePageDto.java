package com.anipick.backend.community.dto;

import com.anipick.backend.common.dto.CursorDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * GET /api/community/lounge/today 응답.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoungePageDto {
    private Long count;
    private CursorDto cursor;
    private List<LoungeSeriesItemDto> series;

    public static LoungePageDto empty() {
        return new LoungePageDto(0L, CursorDto.of(null), List.of());
    }
}
