package com.anipick.backend.review.service;

import com.anipick.backend.anime.domain.Anime;
import com.anipick.backend.anime.mapper.AnimeMapper;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.review.domain.Review;
import com.anipick.backend.review.dto.RecentReviewItemDto;
import com.anipick.backend.review.dto.RecentReviewPageDto;
import com.anipick.backend.review.dto.ReviewRequest;
import com.anipick.backend.review.dto.SignupRatingRequest;
import com.anipick.backend.review.mapper.RatingMapper;
import com.anipick.backend.review.mapper.RecentReviewMapper;
import com.anipick.backend.review.mapper.ReviewMapper;
import com.anipick.backend.user.mapper.UserAnimeStatusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public void createAndUpdateReview(Long reviewId, ReviewRequest request, Long userId) {
        Review review = reviewMapper.findByReviewId(reviewId, userId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new CustomException(ErrorCode.REVIEW_CONTENT_NOT_PROVIDED);
        }
        if (review.getContent() == null) {
            Long animeId = review.getAnimeId();
            animeMapper.updatePlusReviewCount(animeId);
        }

        reviewMapper.updateReview(reviewId, userId, request);
    }

    @Transactional
    public void createAndUpdateSignupReview(List<SignupRatingRequest> ratingRequests, Long userId) {
        ratingMapper.createSignupReviewRating(userId, ratingRequests);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewMapper.findByReviewId(reviewId, userId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        Long animeId = review.getAnimeId();

        animeMapper.updateMinusReviewCount(animeId);
        reviewMapper.deleteReview(reviewId, userId);
    }
}