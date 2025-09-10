package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class CharacterPickNameDto {
    private Long id;
    private String name;
    private String imageUrl;
}
