package com.anipick.backend.mypage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WatchingAnimesDto {
    private Long animeId;
    private Long userAnimeStatusId;
    private String title;
    private String coverImageUrl;
}
