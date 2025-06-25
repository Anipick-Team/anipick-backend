package com.anipick.backend.review.dto;

import lombok.Getter;

@Getter
public class SignupRatingRequest {
    private Long animeId;
    private Double rating;
}
