package com.anipick.backend.community.dto;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;

import lombok.Getter;

/**
 * POST /api/community/posts/{postId}/comments 요청.
 * parentCommentId 가 null 이면 댓글, 값이 있으면 대댓글(1depth 제한).
 */
@Getter
public class CommentCreateRequest {
    private String content;
    private Long parentCommentId;

    private static final int CONTENT_MAX = 200;

    public void validate() {
        if (content == null || content.isBlank() || content.length() > CONTENT_MAX) {
            throw new CustomException(ErrorCode.COMMUNITY_COMMENT_CONTENT_LENGTH_INVALID);
        }
    }
}
