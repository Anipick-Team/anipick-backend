package com.anipick.backend.mypage.dto;

import lombok.Getter;

/**
 * 내가 작성한 댓글/대댓글 조회 원본. 시리즈 제목 다국어 픽은 MyCommunityCommentDto 에서 수행.
 */
@Getter
public class MyCommunityCommentAllTitleDto {
    private Long commentId;
    private Long postId;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;
    private String postTitle;
    private String content;
    private Long likeCount;
    private String createdAt;
}
