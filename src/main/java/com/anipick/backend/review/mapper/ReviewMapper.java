package com.anipick.backend.review.mapper;

import com.anipick.backend.review.domain.Review;
import com.anipick.backend.review.dto.ReportReviewDto;
import com.anipick.backend.review.dto.ReviewRatingRequest;
import com.anipick.backend.review.dto.ReviewRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
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

    List<Review> findAllByAnimeId(@Param("animeId") Long animeId);

    Review selectReviewByReviewId(@Param(value = "reviewId") Long reviewId);

    ReportReviewDto selectReportReviewByReviewId(
            @Param(value = "userId") Long userId,
            @Param(value = "reviewId") Long reviewId
    );

    void createReviewReport(
            @Param(value = "userId") Long userId,
            @Param(value = "reviewId") Long reviewId
    );
}
