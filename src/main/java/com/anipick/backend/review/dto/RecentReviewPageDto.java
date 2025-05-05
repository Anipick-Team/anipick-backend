package com.anipick.backend.review.dto;

import com.anipick.backend.common.dto.CursorDto;
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

