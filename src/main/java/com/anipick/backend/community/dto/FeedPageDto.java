package com.anipick.backend.community.dto;

import com.anipick.backend.common.dto.CursorDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * GET /api/community/feed/{all|watching|popular} 응답. 무한 스크롤이라 전체 개수는 내려주지 않는다.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedPageDto {
    private CursorDto cursor;
    private List<FeedPostItemDto> posts;
}
