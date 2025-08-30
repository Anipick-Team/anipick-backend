package com.anipick.backend.explore.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SignUpPopularAnimeAllTitleItemDto {
    private Long animeId;
    private Long score;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;
    private List<String> genres;
}
