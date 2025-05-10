package com.anipick.backend.review.controller.dto;

import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.review.service.dto.RecentReviewItemDto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentReviewPageDto {
    private Long count;
    private CursorDto cursor;
    private List<RecentReviewItemDto> reviews;
}

