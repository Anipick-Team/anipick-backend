package com.anipick.backend.review.service;

import com.anipick.backend.anime.domain.Anime;
import com.anipick.backend.anime.mapper.AnimeMapper;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.review.domain.Review;
import com.anipick.backend.review.dto.*;
import com.anipick.backend.review.mapper.RatingMapper;
import com.anipick.backend.review.mapper.RecentReviewMapper;
import com.anipick.backend.review.mapper.ReviewMapper;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.mapper.UserAnimeStatusMapper;
import com.anipick.backend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final RecentReviewMapper recentReviewMapper;
    private final ReviewMapper reviewMapper;
    private final AnimeMapper animeMapper;
    private final RatingMapper ratingMapper;
    private final UserMapper userMapper;
    private final UserAnimeStatusMapper userAnimeStatusMapper;

    private static final DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd");

    @Transactional(readOnly = true)
    public RecentReviewPageDto getRecentReviews(Long userId, Long lastId, Integer size) {

        long total = recentReviewMapper.countRecentReviews(userId);

        List<RecentReviewItemDto> raws =
                recentReviewMapper.selectRecentReviews(userId, lastId, size);

        List<RecentReviewItemDto> items = raws.stream()
                .map(dto -> {
                    LocalDateTime dateTime = LocalDateTime.parse(dto.getCreatedAt(), parser);
                    String formattedDate = dateTime.format(formatter);

                    return RecentReviewItemDto.builder()
                            .reviewId(dto.getReviewId())
                            .userId(dto.getUserId())
                            .animeId(dto.getAnimeId())
                            .animeTitle(dto.getAnimeTitle())
                            .animeCoverImageUrl(dto.getAnimeCoverImageUrl())
                            .rating(dto.getRating())
                            .reviewContent(dto.getReviewContent())
                            .nickname(dto.getNickname())
                            .profileImageUrl(dto.getProfileImageUrl())
                            .createdAt(formattedDate)
                            .likeCount(dto.getLikeCount())
                            .likedByCurrentUser(dto.getLikedByCurrentUser())
                            .isMine(dto.getIsMine())
                            .build();
                }).collect(Collectors.toList());

        int lastIndex = items.size() - 1;
        Long nextLastId = items.isEmpty() ? null : items.get(lastIndex).getReviewId();

        return RecentReviewPageDto.builder()
                .count(total)
                .cursor(CursorDto.of(nextLastId))
                .reviews(items)
                .build();
    }

    @Transactional
    public void createAndUpdateReview(Long animeId, ReviewRequest request, Long userId) {
        Review review = reviewMapper.findByAnimeId(animeId, userId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new CustomException(ErrorCode.REVIEW_CONTENT_NOT_PROVIDED);
        }
        if (review.getContent() == null) {
            animeMapper.updatePlusReviewCount(animeId);
        }
      
        Long reviewId = review.getReviewId();
        updateReviewAverageScore(animeId);

        reviewMapper.updateReview(reviewId, userId, request);
    }

    @Transactional
    public void createAndUpdateSignupReview(List<SignupRatingRequest> ratingRequests, Long userId) {
        if (ratingRequests == null || ratingRequests.isEmpty()) {
            return;
        }

        ratingMapper.createSignupReviewRating(userId, ratingRequests);
        userMapper.updateReviewCompletedYn(userId);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewMapper.findByReviewId(reviewId, userId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        Long animeId = review.getAnimeId();

        animeMapper.updateMinusReviewCount(animeId);
        reviewMapper.deleteReview(reviewId, userId);

        updateReviewAverageScore(animeId);
    }

    private void updateReviewAverageScore(Long animeId) {
        List<Review> reviewsByAnimeId = reviewMapper.findAllByAnimeId(animeId);
        Double ratingAveraging = reviewsByAnimeId.stream()
                .collect(Collectors.averagingDouble(Review::getRating));

        animeMapper.updateReviewAverageScore(animeId, ratingAveraging);
    }

    @Transactional
    public void reportReview(Long userId, Long reviewId) {
        Review reviewById = reviewMapper.selectReviewByReviewId(reviewId);
        ReportReviewDto reportReview = reviewMapper.selectReportReviewByReviewId(userId, reviewId);

        if (reviewById == null) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }

        Long reportedUserId = reviewById.getUserId();
        if (reportedUserId.equals(userId)) {
            throw new CustomException(ErrorCode.SELF_REVIEW_REPORT_ERROR);
        }

        if (reportReview != null) {
            throw new CustomException(ErrorCode.ALREADY_REPORT_REVIEW);
        }

        try {
            reviewMapper.createReviewReport(userId, reviewId);
        } catch (DuplicateKeyException e) {
            throw new CustomException(ErrorCode.ALREADY_REPORT_REVIEW);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public MyReviewProviderResultDto getAnimeMyReview(Long animeId, Long userId) {
        MyReviewProviderResultDto result = reviewMapper.selectAnimeByMyReview(animeId, userId);
        if (result == null) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }
        if (result.getContent() == null) {
            return MyReviewProviderResultDto.nonContent(result);
        }
        return MyReviewProviderResultDto.of(result);
    }
}