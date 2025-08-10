package com.anipick.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class UserAnimeStatusInsertRequest {
    private Long userId;
    private Long animeId;
    private String status;
}
