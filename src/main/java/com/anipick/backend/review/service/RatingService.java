package com.anipick.backend.review.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.recommendation.domain.UserRecommendMode;
import com.anipick.backend.recommendation.domain.UserState;
import com.anipick.backend.recommendation.mapper.UserStateMapper;
import com.anipick.backend.review.dto.ReviewRatingRequest;
import com.anipick.backend.review.mapper.RatingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingMapper ratingMapper;
    private final UserStateMapper userStateMapper;

    @Transactional
    public void createReviewRating(Long animeId, ReviewRatingRequest request, Long userId) {
        boolean present = ratingMapper.findByAnimeId(animeId, userId).isPresent();
        if (present) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }
        UserState byUserId = userStateMapper.findByUserId(userId);
        if (byUserId == null) {
            userStateMapper.insertInitialState(userId, UserRecommendMode.RECENT_HIGH, animeId);
        } else {
            userStateMapper.updateMode(userId, UserRecommendMode.RECENT_HIGH, animeId);
        }
        ratingMapper.createReviewRating(animeId, userId, request);
    }

    @Transactional
    public void updateReviewRating(Long reviewId, ReviewRatingRequest request, Long userId) {
        ratingMapper.findByReviewId(reviewId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        ratingMapper.updateRating(reviewId, userId, request);
    }

    @Transactional
    public void deleteReviewRating(Long reviewId, Long userId) {
        ratingMapper.findByReviewId(reviewId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        ratingMapper.deleteRating(reviewId, userId);
    }
}
