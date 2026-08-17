package com.anipick.backend.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * POST /api/community/posts/{postId}/comments 응답.
 */
@Getter
@AllArgsConstructor
public class CommentCreateResultDto {
    private Long commentId;

    public static CommentCreateResultDto from(Long commentId) {
        return new CommentCreateResultDto(commentId);
    }
}
