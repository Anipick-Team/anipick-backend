package com.anipick.backend.review.dto;

import lombok.Getter;

@Getter
public class ReviewRequest {
    private Double rating;
    private String content;
    private Boolean isSpoiler;
}
