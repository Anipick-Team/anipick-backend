package com.anipick.backend.review.mapper;

import com.anipick.backend.review.domain.Review;
import com.anipick.backend.review.dto.ReviewRatingRequest;
import com.anipick.backend.review.dto.ReviewRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface ReviewMapper {

    Optional<Review> findByReviewId(@Param("animeId") Long animeId, @Param("userId") Long userId);


    void updateReview(
            @Param("reviewId") Long reviewId,
            @Param("userId") Long userId,
            @Param("request") ReviewRequest request
    );

    void deleteReview(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
}
