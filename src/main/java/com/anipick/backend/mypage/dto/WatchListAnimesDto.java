package com.anipick.backend.mypage.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WatchListAnimesDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private LocalDateTime createdAt;
}
