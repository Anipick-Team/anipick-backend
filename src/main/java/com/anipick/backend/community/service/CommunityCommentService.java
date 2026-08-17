package com.anipick.backend.community.service;

import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.community.domain.CommunityComment;
import com.anipick.backend.community.domain.CommunityPost;
import com.anipick.backend.community.dto.CommentCreateRequest;
import com.anipick.backend.community.dto.CommentCreateResultDto;
import com.anipick.backend.community.dto.CommentItemDto;
import com.anipick.backend.community.dto.CommentItemRawDto;
import com.anipick.backend.community.dto.CommentListPageDto;
import com.anipick.backend.community.dto.CommentUpdateRequest;
import com.anipick.backend.community.mapper.CommunityCommentLikeMapper;
import com.anipick.backend.community.mapper.CommunityCommentMapper;
import com.anipick.backend.community.mapper.CommunityPostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityCommentService {

    private final CommunityCommentMapper communityCommentMapper;
    private final CommunityCommentLikeMapper communityCommentLikeMapper;
    private final CommunityPostMapper communityPostMapper;
    private final RedissonClient redissonClient;

    private static final DateTimeFormatter PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy. MM. dd");

    private static final String DELETED_COMMENT_CONTENT = "삭제된 댓글 입니다.";
    private static final String BLOCKED_COMMENT_CONTENT = "차단한 사용자의 댓글 입니다.";
    private static final String REPORTED_COMMENT_CONTENT = "신고한 댓글 입니다.";

    @Transactional(readOnly = true)
    public CommentListPageDto getComments(Long postId, Long lastId, int size, Long userId) {
        findActivePostOrThrow(postId);

        Long total = communityCommentMapper.countVisibleComments(postId, userId);
        List<CommentItemRawDto> parents = communityCommentMapper.selectParentComments(postId, userId, lastId, size);

        Map<Long, List<CommentItemRawDto>> repliesByParent;
        if (parents.isEmpty()) {
            repliesByParent = Collections.emptyMap();
        } else {
            List<Long> parentIds = parents.stream()
                    .map(CommentItemRawDto::getCommentId)
                    .toList();
            repliesByParent = communityCommentMapper.selectRepliesByParentIds(parentIds, userId).stream()
                    .collect(Collectors.groupingBy(CommentItemRawDto::getParentCommentId));
        }

        List<CommentItemDto> comments = parents.stream()
                .map(parent -> toParentDto(parent, repliesByParent.getOrDefault(
                        parent.getCommentId(), Collections.emptyList()), userId))
                .toList();

        CursorDto cursor = parents.isEmpty()
                ? CursorDto.of(null)
                : CursorDto.of(parents.get(parents.size() - 1).getCommentId());

        return CommentListPageDto.builder()
                .count(total)
                .cursor(cursor)
                .comments(comments)
                .build();
    }

    @Transactional
    public CommentCreateResultDto createComment(Long postId, CommentCreateRequest request, Long userId) {
        request.validate();
        findActivePostOrThrow(postId);

        Long parentCommentId = request.getParentCommentId();
        if (parentCommentId != null) {
            CommunityComment parent = communityCommentMapper.selectCommentForCheck(parentCommentId);
            // 없거나, 삭제됐거나, 다른 게시글의 댓글이면 부모로 쓸 수 없음
            if (parent == null || parent.getDeletedAt() != null || !parent.getPostId().equals(postId)) {
                throw new CustomException(ErrorCode.COMMUNITY_PARENT_COMMENT_NOT_FOUND);
            }
            // 1depth 제한: 대댓글에는 대댓글을 달 수 없음
            if (parent.getParentCommentId() != null) {
                throw new CustomException(ErrorCode.COMMUNITY_COMMENT_NESTING_INVALID);
            }
        }

        CommunityComment comment = CommunityComment.builder()
                .postId(postId)
                .userId(userId)
                .parentCommentId(parentCommentId)
                .content(request.getContent())
                .build();
        communityCommentMapper.insertComment(comment);
        communityPostMapper.increaseCommentCount(postId);

        return CommentCreateResultDto.from(comment.getCommentId());
    }

    @Transactional
    public void updateComment(Long commentId, CommentUpdateRequest request, Long userId) {
        request.validate();

        CommunityComment comment = findActiveCommentOrThrow(commentId);
        if (!comment.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMUNITY_COMMENT_NOT_OWNER);
        }

        communityCommentMapper.updateComment(commentId, userId, request);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        CommunityComment comment = findActiveCommentOrThrow(commentId);
        if (!comment.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMUNITY_COMMENT_NOT_OWNER);
        }

        // Soft delete. 하위 대댓글은 유지되며, 목록에서 "삭제된 댓글 입니다" 로 마스킹된다.
        // 동시 더블 삭제 시 늦은 쪽은 영향 행 0 → comment_count 를 중복 감소시키지 않는다.
        int affected = communityCommentMapper.softDeleteComment(commentId, userId);
        if (affected > 0) {
            communityPostMapper.decreaseCommentCount(comment.getPostId());
        }
    }

    @Transactional
    public void likeComment(Long userId, Long commentId) {
        findActiveCommentOrThrow(commentId);

        RLock lock = redissonClient.getLock("communityComment:" + commentId + ":likeLock");
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(1, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("락 획득 실패");
                throw new CustomException(ErrorCode.GET_LOCK_FAILED);
            }

            try {
                communityCommentLikeMapper.insertLikeComment(userId, commentId);
            } catch (DuplicateKeyException e) {
                throw new CustomException(ErrorCode.ALREADY_LIKE_DATA);
            }

            communityCommentMapper.increaseLikeCount(commentId);
        } catch (InterruptedException e) {
            log.error("락 인터럽트 : {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            if (isLocked) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public void unlikeComment(Long userId, Long commentId) {
        findActiveCommentOrThrow(commentId);

        RLock lock = redissonClient.getLock("communityComment:" + commentId + ":likeLock");
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(1, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("락 획득 실패");
                throw new CustomException(ErrorCode.GET_LOCK_FAILED);
            }

            boolean isLiked = communityCommentLikeMapper.selectUserLikeComment(userId, commentId);
            if (!isLiked) {
                throw new CustomException(ErrorCode.LIKE_DATA_NOT_FOUND);
            }

            communityCommentLikeMapper.deleteLikeComment(userId, commentId);
            communityCommentMapper.decreaseLikeCount(commentId);
        } catch (InterruptedException e) {
            log.error("락 인터럽트 : {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            if (isLocked) {
                lock.unlock();
            }
        }
    }

    private CommentItemDto toParentDto(CommentItemRawDto raw, List<CommentItemRawDto> replies, Long userId) {
        List<CommentItemDto> replyDtos = replies.stream()
                .map(reply -> toDto(reply, userId, null))
                .toList();

        // 숨김 대상(삭제/차단/신고) 부모는 대댓글 유지를 위해 마스킹으로 포함된 행
        if (raw.getIsDeleted() || raw.getIsBlocked() || raw.getIsReported()) {
            return CommentItemDto.builder()
                    .commentId(raw.getCommentId())
                    .userId(null)
                    .nickname(null)
                    .profileImageId(null)
                    .content(maskedContent(raw))
                    .likeCount(0L)
                    .isLiked(false)
                    .isMine(false)
                    .isDeleted(true)
                    .isEdited(false)
                    .createdAt(formatCreatedAt(raw.getCreatedAt()))
                    .replies(replyDtos)
                    .build();
        }

        return toDto(raw, userId, replyDtos);
    }

    private CommentItemDto toDto(CommentItemRawDto raw, Long userId, List<CommentItemDto> replies) {
        return CommentItemDto.builder()
                .commentId(raw.getCommentId())
                .userId(raw.getUserId())
                .nickname(raw.getNickname())
                .profileImageId(raw.getProfileImageId())
                .content(raw.getContent())
                .likeCount(raw.getLikeCount())
                .isLiked(raw.getIsLiked())
                .isMine(raw.getUserId().equals(userId))
                .isDeleted(false)
                .isEdited(raw.getIsEdited())
                .createdAt(formatCreatedAt(raw.getCreatedAt()))
                .replies(replies)
                .build();
    }

    private String maskedContent(CommentItemRawDto raw) {
        if (raw.getIsDeleted()) {
            return DELETED_COMMENT_CONTENT;
        }
        if (raw.getIsBlocked()) {
            return BLOCKED_COMMENT_CONTENT;
        }
        return REPORTED_COMMENT_CONTENT;
    }

    private String formatCreatedAt(String createdAt) {
        return LocalDateTime.parse(createdAt, PARSER).format(FORMATTER);
    }

    private void findActivePostOrThrow(Long postId) {
        CommunityPost post = communityPostMapper.selectPostForCheck(postId);
        if (post == null || post.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.COMMUNITY_POST_NOT_FOUND);
        }
    }

    private CommunityComment findActiveCommentOrThrow(Long commentId) {
        CommunityComment comment = communityCommentMapper.selectCommentForCheck(commentId);
        if (comment == null || comment.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.COMMUNITY_COMMENT_NOT_FOUND);
        }
        return comment;
    }
}
