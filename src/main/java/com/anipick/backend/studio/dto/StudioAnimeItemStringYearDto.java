package com.anipick.backend.studio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudioAnimeItemStringYearDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private String seasonYear;

    public static StudioAnimeItemStringYearDto from(StudioAnimeItemDto dto) {
        String yearStr;
        if (dto.getSeasonYear() != null) {
            yearStr = dto.getSeasonYear().toString();
        } else {
            yearStr = null;
        }
        return new StudioAnimeItemStringYearDto(
                dto.getAnimeId(),
                dto.getTitle(),
                dto.getCoverImageUrl(),
                yearStr
        );
    }
}
