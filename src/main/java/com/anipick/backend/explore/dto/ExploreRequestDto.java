package com.anipick.backend.explore.dto;

import com.anipick.backend.anime.domain.RangeDate;
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
    private String startDate;
    private String endDate;
    private List<Long> genres;
    private Integer genresSize;
    private String genreOp;
    private List<String> types;
    private Integer typeConvertSize;
    private String type;
    private String sort;
    private String orderByQuery;
    private Long lastId;
    private Double lastValue;
    private int size;

    public static ExploreRequestDto of(
        RangeDate rangeDate,
        List<Long> genres,
        Integer genresSize,
        String genreOp,
        List<String> types,
        Integer typeConvertSize,
        String type,
        String sort,
        String orderByQuery,
        Long lastId,
        Double lastValue,
        int size
    ) {
        return new ExploreRequestDto(
            rangeDate.getStartDate(), rangeDate.getEndDate(),
            genres, genresSize, genreOp,
            types, typeConvertSize, type,
            sort, orderByQuery,
            lastId, lastValue, size
        );
    }

    public static ExploreRequestDto dateNullOf(
        List<Long> genres,
        Integer genresSize,
        String genreOp,
        List<String> types,
        Integer typeConvertSize,
        String type,
        String sort,
        String orderByQuery,
        Long lastId,
        Double lastValue,
        int size
    ) {
        return new ExploreRequestDto(
            null, null,
            genres, genresSize, genreOp,
            types, typeConvertSize, type,
            sort, orderByQuery,
            lastId, lastValue, size
        );
    }
}
