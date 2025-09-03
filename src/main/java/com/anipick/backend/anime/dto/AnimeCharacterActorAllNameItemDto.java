package com.anipick.backend.anime.dto;

import lombok.Getter;

@Getter
public class AnimeCharacterActorAllNameItemDto {
    private Long characterId;
    private String characterNameKor;
    private String characterNameEng;
    private String characterImageUrl;

    private Long voiceActorId;
    private String voiceActorNameKor;
    private String voiceActorNameEng;
    private String voiceActorImageUrl;
}
