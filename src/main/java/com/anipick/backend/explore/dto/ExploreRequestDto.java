package com.anipick.backend.explore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExploreRequestDto {
    private Integer year;
    private Integer season;
    private List<Long> genres;
    private Integer genresSize;
    private String genreOp;
    private List<String> types;
    private Integer typeConvertSize;
    private String type;
    private String sort;
    private String orderByQuery;
    private Long lastId;
    private Integer lastValue;
    private int size;
}
