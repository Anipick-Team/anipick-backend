package com.anipick.backend.community.dto;

import com.anipick.backend.anime.dto.GenreDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * GET /api/community/boards/by-anime/{animeId} 응답.
 * hasBoard=false(게시판 없음)면 postCount 는 응답에서 제외된다(null → NON_NULL).
 */
@Getter
@Builder
@AllArgsConstructor
public class BoardByAnimeResultDto {
    private Boolean hasBoard;
    private Long seriesId;
    private String title;
    private String coverImageUrl;
    private List<GenreDto> genres;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long postCount;
}
