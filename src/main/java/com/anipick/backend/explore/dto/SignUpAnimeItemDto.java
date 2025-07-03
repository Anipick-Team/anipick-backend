package com.anipick.backend.explore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class SignUpAnimeItemDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private List<String> genres;
}
