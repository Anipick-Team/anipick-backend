package com.anipick.backend.community.dto;

import lombok.Getter;

/**
 * POST /api/community/posts 요청.
 */
@Getter
public class PostCreateRequest extends PostWriteRequest {
    private Long seriesId;
}
