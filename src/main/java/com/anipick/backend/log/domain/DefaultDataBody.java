package com.anipick.backend.log.domain;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultDataBody {
    private ContentType type;
    private String content;
    private int position;

    public static DefaultDataBody createAnimeData(String content, int position) {
        return new DefaultDataBody(ContentType.ANIME, content, position);
    }

    private enum ContentType {
        ANIME
    }
}