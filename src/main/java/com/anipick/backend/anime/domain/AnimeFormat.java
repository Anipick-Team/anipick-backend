package com.anipick.backend.anime.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnimeFormat {
    TV("TVA"),
    TV_SHORT("TVA"),
    MOVIE("극장판"),
    SPECIAL("OVA"),
    OVA("OVA"),
    ONA("TVA");

    private final String frontType;
}
