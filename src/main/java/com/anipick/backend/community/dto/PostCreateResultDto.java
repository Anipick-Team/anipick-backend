package com.anipick.backend.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * POST /api/community/posts 응답.
 */
@Getter
@AllArgsConstructor
public class PostCreateResultDto {
    private Long postId;

    public static PostCreateResultDto from(Long postId) {
        return new PostCreateResultDto(postId);
    }
}
