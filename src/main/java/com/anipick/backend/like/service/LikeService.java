package com.anipick.backend.like.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.like.mapper.LikeMapper;
import com.anipick.backend.review.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeMapper likeMapper;
    private final ReviewMapper reviewMapper;

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

    public void likeReview(Long userId, Long reviewId) {
        try {
            likeMapper.insertLikeReview(userId, reviewId);
            reviewMapper.updatePlusReviewLikeCount(reviewId);
        } catch (DuplicateKeyException e) {
            throw new CustomException(ErrorCode.ALREADY_LIKE_DATA);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void notLikeReview(Long userId, Long reviewId) {
        Boolean isLiked = likeMapper.selectUserLikeReview(userId, reviewId);
        if (isLiked) {
            likeMapper.deleteLikeReview(userId, reviewId);
            reviewMapper.updateMinusReviewLikeCount(reviewId);
        } else {
            throw new CustomException(ErrorCode.LIKE_DATA_NOT_FOUND);
        }
    }
}
