package com.anipick.backend.community.dto;

import com.anipick.backend.common.dto.CursorDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.List;

/**
 * GET /api/community/explore/boards 응답.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExploreBoardPageDto {
    private Long count;
    private CursorDto cursor;
    private List<ExploreBoardItemDto> boards;
}
