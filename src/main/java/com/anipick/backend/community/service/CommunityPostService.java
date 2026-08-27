package com.anipick.backend.community.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.common.util.LocalizationUtil;
import com.anipick.backend.community.domain.CommunityPost;
import com.anipick.backend.community.dto.PostCreateRequest;
import com.anipick.backend.community.dto.PostCreateResultDto;
import com.anipick.backend.community.dto.PostDetailDto;
import com.anipick.backend.community.dto.PostDetailRawDto;
import com.anipick.backend.community.dto.PostUpdateRequest;
import com.anipick.backend.community.mapper.CommunityBoardMapper;
import com.anipick.backend.community.mapper.CommunityPostLikeMapper;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostMapper communityPostMapper;
    private final CommunityPostLikeMapper communityPostLikeMapper;
    private final CommunityBoardMapper communityBoardMapper;
    private final RedissonClient redissonClient;

    private static final DateTimeFormatter PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy. MM. dd");

    @Transactional
    public PostCreateResultDto createPost(PostCreateRequest request, Long userId) {
        request.validate();

        if (!communityBoardMapper.existsSeries(request.getSeriesId())) {
            throw new CustomException(ErrorCode.SERIES_NOT_FOUND);
        }

        CommunityPost post = CommunityPost.builder()
                .seriesId(request.getSeriesId())
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .isSpoiler(request.getSpoilerOrDefault())
                .build();
        communityPostMapper.insertPost(post);

        List<Long> imageIds = request.getImageIds();
        if (imageIds != null && !imageIds.isEmpty()) {
            communityPostMapper.insertPostImages(post.getPostId(), imageIds);
        }

        return PostCreateResultDto.from(post.getPostId());
    }

    @Transactional
    public PostDetailDto getPostDetail(Long postId, Long userId) {
        // 조회수 1 증가 (삭제된 글은 UPDATE 조건에서 걸러져 증가하지 않음)
        communityPostMapper.increaseViewCount(postId);

        PostDetailRawDto raw = communityPostMapper.selectPostDetail(postId, userId);
        if (raw == null) {
            throw new CustomException(ErrorCode.COMMUNITY_POST_NOT_FOUND);
        }

        List<Long> imageIds = communityPostMapper.selectPostImageIds(postId);
        String seriesTitle = LocalizationUtil.pickTitle(
                null, raw.getSeriesTitleKor(), raw.getSeriesTitleEng(), raw.getSeriesTitleRom(), raw.getSeriesTitleNat());

        return PostDetailDto.builder()
                .postId(raw.getPostId())
                .seriesId(raw.getSeriesId())
                .seriesTitle(seriesTitle)
                .userId(raw.getUserId())
                .nickname(raw.getNickname())
                .profileImageId(raw.getProfileImageId())
                .title(raw.getTitle())
                .content(raw.getContent())
                .isSpoiler(raw.getIsSpoiler())
                .imageIds(imageIds)
                .viewCount(raw.getViewCount())
                .likeCount(raw.getLikeCount())
                .commentCount(raw.getCommentCount())
                .isLiked(raw.getIsLiked())
                .isMine(raw.getUserId().equals(userId))
                .isAuthorBlocked(raw.getIsAuthorBlocked())
                .createdAt(LocalDateTime.parse(raw.getCreatedAt(), PARSER).format(FORMATTER))
                .build();
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, Long userId) {
        request.validate();

        CommunityPost post = findActivePostOrThrow(postId);
        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMUNITY_POST_NOT_OWNER);
        }

        communityPostMapper.updatePost(postId, userId, request);

        // 첨부 이미지 전량 교체 (기존 매핑 삭제 후 재삽입)
        communityPostMapper.deletePostImages(postId);
        List<Long> imageIds = request.getImageIds();
        if (imageIds != null && !imageIds.isEmpty()) {
            communityPostMapper.insertPostImages(postId, imageIds);
        }
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        CommunityPost post = findActivePostOrThrow(postId);
        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMUNITY_POST_NOT_OWNER);
        }
        // Soft delete. 댓글/대댓글은 그대로 유지.
        communityPostMapper.softDeletePost(postId, userId);
    }

    @Transactional
    public void likePost(Long userId, Long postId) {
        findActivePostOrThrow(postId);

        RLock lock = redissonClient.getLock("communityPost:" + postId + ":likeLock");
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(1, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("락 획득 실패");
                throw new CustomException(ErrorCode.GET_LOCK_FAILED);
            }

            try {
                communityPostLikeMapper.insertLikePost(userId, postId);
            } catch (DuplicateKeyException e) {
                throw new CustomException(ErrorCode.ALREADY_LIKE_DATA);
            }

            communityPostMapper.increaseLikeCount(postId);
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
    public void unlikePost(Long userId, Long postId) {
        findActivePostOrThrow(postId);

        RLock lock = redissonClient.getLock("communityPost:" + postId + ":likeLock");
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(1, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("락 획득 실패");
                throw new CustomException(ErrorCode.GET_LOCK_FAILED);
            }

            boolean isLiked = communityPostLikeMapper.selectUserLikePost(userId, postId);
            if (!isLiked) {
                throw new CustomException(ErrorCode.LIKE_DATA_NOT_FOUND);
            }

            communityPostLikeMapper.deleteLikePost(userId, postId);
            communityPostMapper.decreaseLikeCount(postId);
        } catch (InterruptedException e) {
            log.error("락 인터럽트 : {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            if (isLocked) {
                lock.unlock();
            }
        }
    }

    private CommunityPost findActivePostOrThrow(Long postId) {
        CommunityPost post = communityPostMapper.selectPostForCheck(postId);
        if (post == null || post.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.COMMUNITY_POST_NOT_FOUND);
        }
        return post;
    }
}
