package com.anipick.backend.anime.dto;

import com.anipick.backend.anime.domain.AnimeCharacterRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class AnimeCharacterActorItemWithRoleDto {
    private CharacterPickNameDto character;
    private VoiceActorPickNameDto voiceActor;
    private AnimeCharacterRole role;
}

