package com.anipick.backend.anime.dto;

import java.util.List;

import com.anipick.backend.common.dto.CursorDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class AnimeCharacterActorPageDto {
    private CursorDto cursor;
    private List<AnimeCharacterActorItemWithRoleDto> characters;
}