package com.anipick.backend.review.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReportReviewDto {
    private Long reportedReviewId;
    private Long reviewId;
    private Long userId;
    private LocalDateTime createdAt;
    private String message;
}
