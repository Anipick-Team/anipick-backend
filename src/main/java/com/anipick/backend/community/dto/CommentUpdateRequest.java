package com.anipick.backend.community.dto;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;

import lombok.Getter;

/**
 * PATCH /api/community/comments/{commentId} 요청.
 */
@Getter
public class CommentUpdateRequest {
    private String content;

    private static final int CONTENT_MAX = 200;

    public void validate() {
        if (content == null || content.isBlank() || content.length() > CONTENT_MAX) {
            throw new CustomException(ErrorCode.COMMUNITY_COMMENT_CONTENT_LENGTH_INVALID);
        }
    }
}
