package com.anipick.backend.anime.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ComingSoonRequestDto {
    private Long lastId;
    private String lastValue;
    private int size;
    private int includeAdult;
    private String orderByQuery;

    public static ComingSoonRequestDto of(Long lastId, String lastValue, int size, int includeAdult, String orderByQuery) {
        return new ComingSoonRequestDto(lastId, lastValue, size, includeAdult, orderByQuery);
    }
}
