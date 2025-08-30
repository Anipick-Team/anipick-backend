package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class AnimeCharacterActorItemPickNameDto {
    private CharacterPickNameDto character;
    private VoiceActorPickNameDto voiceActor;
}
