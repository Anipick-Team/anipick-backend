package com.anipick.backend.anime.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Season {
    WINTER(1), SPRING(2), SUMMER(3), FALL(4);

    private final int code;
}
