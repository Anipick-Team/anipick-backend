package com.anipick.backend.review.mapper;

import com.anipick.backend.review.domain.Review;
import com.anipick.backend.review.dto.MyReviewProviderResultDto;
import com.anipick.backend.review.dto.ReportReviewDto;
import com.anipick.backend.review.dto.ReviewRatingRequest;
import com.anipick.backend.review.dto.ReviewRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReviewMapper {

    Optional<Review> findByAnimeId(@Param("animeId") Long animeId, @Param("userId") Long userId);

    Optional<Review> findByReviewId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    void updateReview(
            @Param("reviewId") Long reviewId,
            @Param("userId") Long userId,
            @Param("request") ReviewRequest request
    );

    void deleteReview(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    List<Review> findAllByAnimeId(@Param("animeId") Long animeId);

    List<Double> findAllRatingByAnimeId(@Param(value = "animeId") Long animeId);

    Review selectReviewByReviewId(@Param(value = "reviewId") Long reviewId);

    ReportReviewDto selectReportReviewByReviewId(
            @Param(value = "userId") Long userId,
            @Param(value = "reviewId") Long reviewId
    );

    void createReviewReport(
            @Param(value = "userId") Long userId,
            @Param(value = "reviewId") Long reviewId
    );
  
    void updatePlusReviewLikeCount(
            @Param(value = "reviewId") Long reviewId
    );

    void updateMinusReviewLikeCount(
            @Param(value = "reviewId") Long reviewId
    );

    MyReviewProviderResultDto selectAnimeByMyReview(
            @Param(value = "animeId") Long animeId,
            @Param(value = "userId") Long userId
    );
}
