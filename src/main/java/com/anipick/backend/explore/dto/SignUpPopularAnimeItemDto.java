package com.anipick.backend.explore.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SignUpPopularAnimeItemDto  {
    private Long animeId;
    private Long score;
    private String title;
    private String coverImageUrl;
    private List<String> genres;

    public static SignUpPopularAnimeItemDto animeTitleTranslationPick(SignUpPopularAnimeAllTitleItemDto dto) {
        String title = LocalizationUtil.pickTitle(
                dto.getTitleKor(),
                dto.getTitleEng(),
                dto.getTitleRom(),
                dto.getTitleNat()
        );
        return new SignUpPopularAnimeItemDto(
                dto.getAnimeId(),
                dto.getScore(),
                title,
                dto.getCoverImageUrl(),
                dto.getGenres()
        );
    }
}
