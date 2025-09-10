package com.anipick.backend.explore.dto;

import com.anipick.backend.anime.domain.RangeDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class SignUpAnimeExploreSearchRequestDto {
    private String query;
    private String startDate;
    private String endDate;
    private Long genres;
    private Long lastId;
    private int size;
    private String nowString;

    public static SignUpAnimeExploreSearchRequestDto of(
            RangeDate rangeDate,
            String query,
            Long genres,
            Long lastId,
            int size,
            String nowString
    ) {
        return new SignUpAnimeExploreSearchRequestDto(
                query,
                rangeDate.getStartDate(),
                rangeDate.getEndDate(),
                genres,
                lastId,
                size,
                nowString
        );
    }

    public static SignUpAnimeExploreSearchRequestDto dateNullOf(
            String query,
            Long genres,
            Long lastId,
            int size,
            String nowString
    ) {
        return new SignUpAnimeExploreSearchRequestDto(
                query,
                null,
                null,
                genres,
                lastId,
                size,
                nowString
        );
    }
}
