package com.anipick.backend.anime.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Season {
    WINTER(1, "1분기"),
    SPRING(2, "2분기"),
    SUMMER(3, "3분기"),
    FALL(4, "4분기");

    private final int code;
    private final String name;
}
