package com.anipick.backend.community.mapper;

import com.anipick.backend.community.domain.CommunityComment;
import com.anipick.backend.community.dto.CommentItemRawDto;
import com.anipick.backend.community.dto.CommentUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityCommentMapper {

    /**
     * 표시 가능한 댓글+대댓글 전체 수(삭제/차단/신고 제외).
     */
    Long countVisibleComments(
            @Param("postId") Long postId,
            @Param("userId") Long userId
    );

    /**
     * 부모 댓글 페이지 (comment_id ASC 커서).
     * 삭제/차단/신고된 부모라도 표시 가능한 대댓글이 있으면 포함(서비스에서 마스킹).
     */
    List<CommentItemRawDto> selectParentComments(
            @Param("postId") Long postId,
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );

    /**
     * 부모 댓글 목록에 속한 표시 가능한 대댓글 전체 (parent_comment_id, comment_id ASC).
     */
    List<CommentItemRawDto> selectRepliesByParentIds(
            @Param("parentIds") List<Long> parentIds,
            @Param("userId") Long userId
    );

    /**
     * 소유자/존재 확인용. 삭제 여부와 관계없이 조회(서비스에서 판단).
     */
    CommunityComment selectCommentForCheck(@Param("commentId") Long commentId);

    void insertComment(CommunityComment comment);

    void updateComment(
            @Param("commentId") Long commentId,
            @Param("userId") Long userId,
            @Param("request") CommentUpdateRequest request
    );

    /**
     * @return 영향 행 수. 동시 더블 삭제 시 늦은 쪽은 0 → comment_count 이중 감소 방지에 사용.
     */
    int softDeleteComment(
            @Param("commentId") Long commentId,
            @Param("userId") Long userId
    );

    void increaseLikeCount(@Param("commentId") Long commentId);

    void decreaseLikeCount(@Param("commentId") Long commentId);
}
