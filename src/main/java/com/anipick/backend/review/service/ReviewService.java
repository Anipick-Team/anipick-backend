package com.anipick.backend.review.service;

import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.review.service.dto.RecentReviewItemDto;
import com.anipick.backend.review.controller.dto.RecentReviewPageDto;
import com.anipick.backend.review.mapper.RecentReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final RecentReviewMapper reviewMapper;

    private static final DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd");


    @Transactional(readOnly = true)
    public RecentReviewPageDto getRecentReviews(Long userId, Long lastId, Integer size) {

        long total = reviewMapper.countRecentReviews(userId);

        List<RecentReviewItemDto> raws =
                reviewMapper.selectRecentReviews(userId, lastId, size);

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
}