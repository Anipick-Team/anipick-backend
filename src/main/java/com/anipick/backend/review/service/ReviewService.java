package com.anipick.backend.review.service;

import com.anipick.backend.review.dto.CursorDto;
import com.anipick.backend.review.dto.RecentReviewItemDto;
import com.anipick.backend.review.dto.RecentReviewPageDto;
import com.anipick.backend.review.mapper.RecentReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final RecentReviewMapper reviewMapper;

    @Transactional(readOnly = true)
    public RecentReviewPageDto getRecentReviews(Long userId, Long lastId, Integer size) {

        long total = reviewMapper.countRecentReviews(userId);

        List<RecentReviewItemDto> items =
                reviewMapper.selectRecentReviews(userId, lastId, size);

        return RecentReviewPageDto.builder()
                .count(total)
                .cursor(new CursorDto(
                        items.isEmpty() ? null : items.get(items.size() - 1).getReviewId()
                ))
                .reviews(items)
                .build();
    }
}