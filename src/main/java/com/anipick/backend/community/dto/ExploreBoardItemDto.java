package com.anipick.backend.community.dto;

import com.anipick.backend.anime.dto.GenreDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.List;

/**
 * 탐색 게시판 목록 아이템.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExploreBoardItemDto {
    private Long seriesId;
    private String title;
    private String coverImageUrl;
    private List<GenreDto> genres;
}
