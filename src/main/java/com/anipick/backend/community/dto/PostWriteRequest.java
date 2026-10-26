package com.anipick.backend.community.dto;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;

import lombok.Getter;

import java.util.List;

/**
 * 게시글 작성/수정 요청 공통 필드와 검증.
 */
@Getter
public abstract class PostWriteRequest {
    private String title;
    private String content;
    private Boolean isSpoiler;
    private List<Long> imageIds;

    private static final int TITLE_MAX = 50;
    private static final int CONTENT_MAX = 1000;
    private static final int IMAGE_MAX = 5;

    public void validate() {
        if (title == null || title.isBlank() || title.length() > TITLE_MAX) {
            throw new CustomException(ErrorCode.COMMUNITY_POST_TITLE_LENGTH_INVALID);
        }
        // 내용은 0자 허용. null·공백만 있는 내용은 "" 로 저장 (content 컬럼 NOT NULL)
        if (content == null || content.isBlank()) {
            content = "";
        }
        if (content.length() > CONTENT_MAX) {
            throw new CustomException(ErrorCode.COMMUNITY_POST_CONTENT_LENGTH_INVALID);
        }
        if (imageIds != null && imageIds.size() > IMAGE_MAX) {
            throw new CustomException(ErrorCode.COMMUNITY_POST_IMAGE_COUNT_EXCEEDED);
        }
    }

    public boolean getSpoilerOrDefault() {
        return isSpoiler != null && isSpoiler;
    }
}
