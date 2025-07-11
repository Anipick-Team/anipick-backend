package com.anipick.backend.anime.dto;

import com.anipick.backend.anime.domain.AnimeCharacterRole;

import lombok.Getter;

@Getter
public class AnimeCharacterActorResultDto {
    private CharacterDto character;
    private VoiceActorDto voiceActor;
    private AnimeCharacterRole role;
}