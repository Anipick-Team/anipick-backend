package com.anipick.backend.person.dto;

import lombok.Getter;

@Getter
public class PersonAnimeWorkDto {
    private Long animeId;
    private String animeTitle;
    private Long characterId;
    private String characterName;
    private String characterImageUrl;
}
