package com.anipick.backend.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sort;
    private Long lastId;

    public CursorDto(Long lastId) {
        this.lastId = lastId;
    }

    public static CursorDto of(Long lastId) {
        return new CursorDto(lastId);
    }

    public static CursorDto of(String sort, Long lastId) {
        return new CursorDto(sort, lastId);
    }
}
