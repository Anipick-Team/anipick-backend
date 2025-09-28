package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComingSoonRequestDto {
    private Long lastId;
    private String lastValue;
    private Long size;
    private String orderByQuery;
    private String defaultCoverUrl;

    public static ComingSoonRequestDto of(
            Long lastId,
            String lastValue,
            Long size,
            String orderByQuery,
            String defaultCoverUrl
    ) {
        return new ComingSoonRequestDto(lastId, lastValue, size, orderByQuery, defaultCoverUrl);
    }
}
