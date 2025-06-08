package com.anipick.backend.review.mapper;

import com.anipick.backend.review.domain.Review;
import com.anipick.backend.review.dto.ReviewRatingRequest;
import com.anipick.backend.review.dto.SignupRatingRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RatingMapper {

    Optional<Review> findByAnimeId(@Param("animeId") Long animeId, @Param("userId") Long userId);

    Optional<Review> findByReviewId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    void createReviewRating(
            @Param("animeId") Long animeId,
            @Param("userId") Long userId,
            @Param("request") ReviewRatingRequest request
    );

    void createSignupReviewRating(
            @Param("userId") Long userId,
            @Param("list") List<SignupRatingRequest> requests
    );

    void updateRating(
            @Param("reviewId") Long reviewId,
            @Param("userId") Long userId,
            @Param("request") ReviewRatingRequest request
    );

    void deleteRating(
            @Param("reviewId") Long reviewId,
            @Param("userId") Long userId,
            @Param("request") ReviewRatingRequest request
    );
}
