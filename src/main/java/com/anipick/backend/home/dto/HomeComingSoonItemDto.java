package com.anipick.backend.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class HomeComingSoonItemDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private String releaseDate;
    private List<String> genres;
}
