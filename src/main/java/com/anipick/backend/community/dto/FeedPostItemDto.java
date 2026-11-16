package com.anipick.backend.community.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 커뮤니티 메인 피드 게시글 아이템.
 * 프로필/썸네일 이미지는 imageId 만 내려주고 실제 조회는 /api/image/{imageId} 사용(기존 게시판 목록과 동일).
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedPostItemDto {
    private Long postId;
    private Long seriesId;
    private String seriesTitle;
    private Long userId;
    private String nickname;
    private Long profileImageId;
    private String title;
    private String content;
    private Long thumbnailImageId;
    private Long imageCount;
    private Boolean isSpoiler;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String createdAt;
}
