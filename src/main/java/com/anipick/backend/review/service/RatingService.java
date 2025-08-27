package com.anipick.backend.review.service;

import com.anipick.backend.anime.mapper.AnimeMapper;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.recommendation.domain.UserRecommendMode;
import com.anipick.backend.recommendation.domain.UserRecommendState;
import com.anipick.backend.recommendation.mapper.UserRecommendStateMapper;
import com.anipick.backend.review.domain.Review;
import com.anipick.backend.review.dto.ReviewRatingRequest;
import com.anipick.backend.review.mapper.RatingMapper;
import com.anipick.backend.review.mapper.ReviewMapper;
import com.anipick.backend.user.domain.UserAnimeOfStatus;
import com.anipick.backend.user.domain.UserAnimeStatus;
import com.anipick.backend.user.mapper.UserAnimeStatusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingMapper ratingMapper;
    private final UserAnimeStatusMapper userAnimeStatusMapper;
    private final ReviewMapper reviewMapper;
    private final AnimeMapper animeMapper;
    private final UserRecommendStateMapper userRecommendMapper;
    private final RedissonClient redissonClient;


    @Transactional
    public void createReviewRating(Long animeId, ReviewRatingRequest request, Long userId) {
        boolean present = ratingMapper.findByAnimeId(animeId, userId).isPresent();
        if (present) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }
        ratingMapper.createReviewRating(animeId, userId, request);

        UserAnimeStatus userAnimeStatus = userAnimeStatusMapper.findByUserId(userId, animeId);
        if (userAnimeStatus != null) {
            UserAnimeOfStatus status = userAnimeStatus.getStatus();
            String statusName = status.name();
            if (!statusName.equals(UserAnimeOfStatus.FINISHED.name())) {
                userAnimeStatusMapper.updateUserAnimeStatus(userId, animeId, UserAnimeOfStatus.FINISHED);
            }
        } else {
            userAnimeStatusMapper.createUserAnimeStatus(userId, animeId, UserAnimeOfStatus.FINISHED);
        }

        UserRecommendState byUserId = userRecommendMapper.findByUserId(userId);
        if (byUserId == null) {
            userRecommendMapper.insertInitialState(userId, UserRecommendMode.RECENT_HIGH, animeId);
        } else {
            userRecommendMapper.updateMode(userId, UserRecommendMode.RECENT_HIGH, animeId);
        }

        updateReviewAverageScore(animeId);
    }

    @Transactional
    public void updateReviewRating(Long reviewId, ReviewRatingRequest request, Long userId) {
        Review review = ratingMapper.findByReviewId(reviewId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        ratingMapper.updateRating(reviewId, userId, request);

        Long animeId = review.getAnimeId();

        updateReviewAverageScore(animeId);
    }

    @Transactional
    public void deleteReviewRating(Long reviewId, Long userId) {
        Review review = ratingMapper.findByReviewId(reviewId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        ratingMapper.deleteRating(reviewId, userId);

        Long animeId = review.getAnimeId();

        updateReviewAverageScore(animeId);
    }

    private void updateReviewAverageScore(Long animeId) {
        RLock lock = redissonClient.getLock("anime:" + animeId + ":lock");
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(1, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("락 획득 실패");
                throw new CustomException(ErrorCode.GET_LOCK_FAILED);
            }
            List<Double> ratings = reviewMapper.findAllRatingByAnimeId(animeId);
            Double ratingAveraging = ratings.stream()
                    .collect(Collectors.averagingDouble(Double::doubleValue));

            animeMapper.updateReviewAverageScore(animeId, ratingAveraging);
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
