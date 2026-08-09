package com.anipick.backend.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

/**
 * 게시글 목록 아이템.
 * 프로필/썸네일 이미지는 imageId 만 내려주고 실제 조회는 /api/image/{imageId} 사용.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostListItemDto {
    private Long postId;
    private Long userId;
    private String nickname;
    private Long profileImageId;
    private String title;
    private String content;
    private Long thumbnailImageId;
    private Boolean isSpoiler;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String createdAt;

    /**
     * 인기순 정렬 시 커서(lastValue)로 쓰는 기간 내 좋아요 수. 응답 JSON 에는 노출하지 않음.
     */
    @JsonIgnore
    private Long periodLikeCount;
}
