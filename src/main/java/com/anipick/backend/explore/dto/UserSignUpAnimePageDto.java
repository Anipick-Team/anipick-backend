package com.anipick.backend.explore.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class UserSignUpAnimePageDto {
    private Long count;
    private CursorDto cursor;
    private List<SignUpAnimeItemDto> animes;
}
