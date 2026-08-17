package com.anipick.backend.community.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.List;

/**
 * 댓글/대댓글 응답 아이템. 대댓글은 replies 가 null → 응답에서 제외(NON_NULL).
 * 삭제/차단/신고로 마스킹된 부모 댓글은 userId/nickname/profileImageId 가 null 이고
 * content 가 안내 문구로 치환되며 isDeleted=true 로 내려간다.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentItemDto {
    private Long commentId;
    private Long userId;
    private String nickname;
    private Long profileImageId;
    private String content;
    private Long likeCount;
    private Boolean isLiked;
    private Boolean isMine;
    private Boolean isDeleted;
    private Boolean isEdited;
    private String createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CommentItemDto> replies;
}
