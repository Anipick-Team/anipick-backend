package com.anipick.backend.review.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.review.dto.ReviewRatingRequest;
import com.anipick.backend.review.mapper.RatingMapper;
import com.anipick.backend.user.domain.UserAnimeOfStatus;
import com.anipick.backend.user.domain.UserAnimeStatus;
import com.anipick.backend.user.mapper.UserAnimeStatusMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingMapper ratingMapper;
    private final UserAnimeStatusMapper userAnimeStatusMapper;

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
    }

    @Transactional
    public void updateReviewRating(Long reviewId, ReviewRatingRequest request, Long userId) {
        ratingMapper.findByReviewId(reviewId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        ratingMapper.updateRating(reviewId, userId, request);
    }

    @Transactional
    public void deleteReviewRating(Long reviewId, ReviewRatingRequest request, Long userId) {
        ratingMapper.findByReviewId(reviewId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        ratingMapper.deleteRating(reviewId, userId, request);
    }
}
