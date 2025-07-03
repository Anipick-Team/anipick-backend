package com.anipick.backend.explore.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SignUpPopularAnimeItemDto  {
    private Long animeId;
    private Long popularId;
    private String title;
    private String coverImageUrl;
    private List<String> genres;
}
