package com.anipick.backend.community.dto;

import com.anipick.backend.common.dto.CursorDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.List;

/**
 * GET /api/community/posts/{postId}/comments 응답.
 * count 는 표시 가능한 댓글+대댓글 전체 수(삭제/차단/신고 제외), 페이징은 부모 댓글 기준.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentListPageDto {
    private Long count;
    private CursorDto cursor;
    private List<CommentItemDto> comments;
}
