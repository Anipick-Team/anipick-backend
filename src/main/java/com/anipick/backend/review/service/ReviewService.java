package com.anipick.backend.review.service;

import com.anipick.backend.anime.domain.Anime;
import com.anipick.backend.anime.mapper.AnimeMapper;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.image.domain.ImageDefaults;
import com.anipick.backend.recommendation.mapper.UserRecommendStateMapper;
import com.anipick.backend.review.domain.Review;
import com.anipick.backend.review.dto.*;
import com.anipick.backend.review.mapper.RatingMapper;
import com.anipick.backend.review.mapper.RecentReviewMapper;
import com.anipick.backend.review.mapper.ReviewMapper;
import com.anipick.backend.user.domain.UserAnimeOfStatus;
import com.anipick.backend.user.mapper.UserAnimeStatusMapper;
import com.anipick.backend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    private final UserRecommendStateMapper userRecommendStateMapper;
    private final RedissonClient redissonClient;

    private static final DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd");

    @Transactional(readOnly = true)
    public RecentReviewPageDto getRecentReviews(Long userId, Long lastId, Integer size) {

        long total = recentReviewMapper.countRecentReviews(userId);

        List<RecentReviewItemDto> raws =
                recentReviewMapper.selectRecentReviews(userId, lastId, size)
                        .stream()
                        .map(RecentReviewItemDto::animeTitleTranslationPick)
                        .toList();

        List<RecentReviewItemDto> items = raws.stream()
                .map(dto -> {
                    LocalDateTime dateTime = LocalDateTime.parse(dto.getCreatedAt(), parser);
                    String formattedDate = dateTime.format(formatter);

                    String imageIdStr = dto.getProfileImageUrl();
                    if (imageIdStr == null) {
                        imageIdStr = "-1";
                    }
                    Long imageId = Long.parseLong(imageIdStr);

                    String imageUrlEndpoint = getImageUrlEndpoint(imageId);

                    return RecentReviewItemDto.builder()
                            .reviewId(dto.getReviewId())
                            .userId(dto.getUserId())
                            .animeId(dto.getAnimeId())
                            .animeTitle(dto.getAnimeTitle())
                            .animeCoverImageUrl(dto.getAnimeCoverImageUrl())
                            .rating(dto.getRating())
                            .reviewContent(dto.getReviewContent())
                            .nickname(dto.getNickname())
                            .profileImageUrl(imageUrlEndpoint)
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

        RLock lock = redissonClient.getLock("anime:" + animeId + ":lock");
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(2,  TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("락 획득 실패");
                throw new CustomException(ErrorCode.GET_LOCK_FAILED);
            }

            if (review.getContent() == null) {
                animeMapper.updatePlusReviewCount(animeId);
            }

            List<Review> reviewsByAnimeId = reviewMapper.findAllByAnimeId(animeId);
            Double ratingAveraging = reviewsByAnimeId.stream()
                    .collect(Collectors.averagingDouble(Review::getRating));

            animeMapper.updateReviewAverageScore(animeId, ratingAveraging);

        } catch (InterruptedException e) {
            log.error("락 인터럽트 : {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            if (isLocked) {
                lock.unlock();
            }
        }

        Long reviewId = review.getReviewId();
        reviewMapper.updateReview(reviewId, userId, request);
    }

    @Transactional
    public void createAndUpdateSignupReview(List<SignupRatingRequest> ratingRequests, Long userId) {
        if (ratingRequests == null || ratingRequests.isEmpty()) {
            return;
        }

        ratingMapper.createSignupReviewRating(userId, ratingRequests);

        List<UserAnimeStatusInsertRequest> statusBulkList = ratingRequests.stream()
                .map(r -> UserAnimeStatusInsertRequest.of(
                        userId,
                        r.getAnimeId(),
                        UserAnimeOfStatus.FINISHED.name()
                ))
                .toList();
        userAnimeStatusMapper.createUserAnimeStatusBulk(statusBulkList);

        List<Long> animeIds = ratingRequests.stream()
                .map(SignupRatingRequest::getAnimeId)
                .toList();
        animeMapper.updateReviewAverageScoresByAnimeIds(animeIds);

        userRecommendStateMapper.insertTagBasedState(userId);

        userMapper.updateReviewCompletedYn(userId);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewMapper.findByReviewId(reviewId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        Long animeId = review.getAnimeId();

        Anime anime = animeMapper.selectAnimeByAnimeId(animeId);

        RLock lock = redissonClient.getLock("anime:" + animeId + ":lock");
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(2, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("락 획득 실패");
                throw new CustomException(ErrorCode.GET_LOCK_FAILED);
            }
            if (anime.getReviewCount() > 0) {
                animeMapper.updateMinusReviewCount(animeId);
            }
            reviewMapper.deleteReview(reviewId, userId);

            List<Review> reviewsByAnimeId = reviewMapper.findAllByAnimeId(animeId);
            Double ratingAveraging = reviewsByAnimeId.stream()
                    .collect(Collectors.averagingDouble(Review::getRating));

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

    private void updateReviewAverageScore(Long animeId) {
        RLock lock = redissonClient.getLock("anime:" + animeId + ":lock");
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(2, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("락 획득 실패");
                throw new CustomException(ErrorCode.GET_LOCK_FAILED);
            }
            List<Review> reviewsByAnimeId = reviewMapper.findAllByAnimeId(animeId);
            Double ratingAveraging = reviewsByAnimeId.stream()
                    .collect(Collectors.averagingDouble(Review::getRating));

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

    @Transactional
    public void reportReview(Long userId, Long reviewId, ReviewReportMessageRequest reportMessageRequest) {
        reportMessageRequest.validate();

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

        String requestMessage = reportMessageRequest.getMessage();

        try {
            reviewMapper.createReviewReport(userId, reviewId, requestMessage);
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

    public String getImageUrlEndpoint(Long imageId) {
        return ImageDefaults.IMAGE_ENDPOINT + imageId;
    }
}