package com.anipick.backend.anime.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ComingSoonRequestDto {
    private Long lastId;
    private String lastValue;
    private Long size;
    private Long includeAdult;
    private String orderByQuery;
    private String defaultCoverUrl;

    public static ComingSoonRequestDto of(
            Long lastId,
            String lastValue,
            Long size,
            Long includeAdult,
            String orderByQuery,
            String defaultCoverUrl
    ) {
        return new ComingSoonRequestDto(lastId, lastValue, size, includeAdult, orderByQuery, defaultCoverUrl);
    }
}
