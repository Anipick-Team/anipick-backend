package com.anipick.backend.explore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExploreAllTitleItemDto {
    private Long id;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;
    private Double averageScore;
    private Long score;
}
