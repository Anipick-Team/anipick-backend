package com.anipick.backend.community.dto;

import lombok.Getter;

/**
 * 시리즈별 대표작 장르 벌크 조회 원본. 서비스에서 seriesId 로 그룹핑해 GenreDto 로 변환.
 */
@Getter
public class SeriesGenreRawDto {
    private Long seriesId;
    private Long id;
    private String name;
}
