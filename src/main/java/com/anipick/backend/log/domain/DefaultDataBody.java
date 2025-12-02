package com.anipick.backend.log.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultDataBody {
    private ContentType type;
    private int animeId;
    private int position;

    public static DefaultDataBody createAnimeData(final int animeId, final int position) {
        return new DefaultDataBody(ContentType.ANIME, animeId, position);
    }

    @JsonIgnore
    public String getTypeName() {
        return type.name();
    }

    private enum ContentType {
        ANIME
    }
}