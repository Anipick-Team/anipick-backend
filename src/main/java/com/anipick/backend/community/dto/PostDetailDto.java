package com.anipick.backend.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.List;

/**
 * GET /api/community/posts/{postId} 응답.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDetailDto {
    private Long postId;
    private Long seriesId;
    private String seriesTitle;
    private Long userId;
    private String nickname;
    private Long profileImageId;
    private String title;
    private String content;
    private Boolean isSpoiler;
    private List<Long> imageIds;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Boolean isLiked;
    private Boolean isMine;
    private Boolean isAuthorBlocked;
    private String createdAt;
}
