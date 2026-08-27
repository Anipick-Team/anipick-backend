package com.anipick.backend.community.dto;

import lombok.Getter;

/**
 * 댓글/대댓글 조회 원본. 마스킹(삭제/차단/신고)과 날짜 포맷은 서비스에서 가공.
 * 대댓글 쿼리는 표시 가능한 행만 가져오므로 isDeleted/isBlocked/isReported 가 항상 false.
 */
@Getter
public class CommentItemRawDto {
    private Long commentId;
    private Long parentCommentId;
    private Long userId;
    private String nickname;
    private Long profileImageId;
    private String content;
    private Long likeCount;
    private Boolean isLiked;
    private Boolean isEdited;
    private Boolean isDeleted;
    private Boolean isBlocked;
    private Boolean isReported;
    private String createdAt;
}
