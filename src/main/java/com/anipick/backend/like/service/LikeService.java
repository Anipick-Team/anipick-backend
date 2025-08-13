package com.anipick.backend.like.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.like.mapper.LikeMapper;
import com.anipick.backend.review.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeMapper likeMapper;
    private final ReviewMapper reviewMapper;
    private final RedissonClient redissonClient;


    public void likeAnime(Long userId, Long animeId) {
        try {
            likeMapper.insertLikeAnime(userId, animeId);
        } catch (DuplicateKeyException e) {
            throw new CustomException(ErrorCode.ALREADY_LIKE_DATA);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void notLikeAnime(Long userId, Long animeId) {
        Boolean isLiked = likeMapper.selectUserLikeAnime(userId, animeId);
        if (isLiked) {
            likeMapper.deleteLikeAnime(userId, animeId);
        } else {
            throw new CustomException(ErrorCode.LIKE_DATA_NOT_FOUND);
        }
    }

    public void likeActor(Long userId, Long personId) {
        try {
            likeMapper.insertLikeActor(userId, personId);
        } catch (DuplicateKeyException e) {
            throw new CustomException(ErrorCode.ALREADY_LIKE_DATA);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void notLikeActor(Long userId, Long personId) {
        Boolean isLiked = likeMapper.selectUserLikeActor(userId, personId);
        if (isLiked) {
            likeMapper.deleteLikeActor(userId, personId);
        } else {
            throw new CustomException(ErrorCode.LIKE_DATA_NOT_FOUND);
        }
    }

    @Transactional
    public void likeReview(Long userId, Long reviewId) {
        RLock lock = redissonClient.getLock("review:" + reviewId + ":likeLock");
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(1,  TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("락 획득 실패");
                throw new CustomException(ErrorCode.GET_LOCK_FAILED);
            }

            try {
                likeMapper.insertLikeReview(userId, reviewId);
            } catch (DuplicateKeyException e) {
                throw new CustomException(ErrorCode.ALREADY_LIKE_DATA);
            } catch (Exception e) {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            reviewMapper.updatePlusReviewLikeCount(reviewId);
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
    public void notLikeReview(Long userId, Long reviewId) {
        RLock lock = redissonClient.getLock("review:" + reviewId + ":likeLock");
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(1,  TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("락 획득 실패");
                throw new CustomException(ErrorCode.GET_LOCK_FAILED);
            }
            boolean isLiked = likeMapper.selectUserLikeReview(userId, reviewId);
            if (!isLiked) {
                throw new CustomException(ErrorCode.LIKE_DATA_NOT_FOUND);
            }
            likeMapper.deleteLikeReview(userId, reviewId);
            reviewMapper.updateMinusReviewLikeCount(reviewId);
        } catch (InterruptedException e) {
            log.error("락 인터럽트 : {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            if (isLocked) {
                lock.unlock();
            }
        }
    }
}
