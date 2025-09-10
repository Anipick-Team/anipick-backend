package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RatedAnimesResponse {
    private Long count;
    private CursorDto cursor;
    private List<AnimesReviewDto> reviews;

    public static RatedAnimesResponse from(Long count, CursorDto cursor, List<AnimesReviewDto> reviews) {
        return new RatedAnimesResponse(count, cursor, reviews);
    }
}
