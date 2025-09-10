package com.anipick.backend.home.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class HomeRecentReviewItemDto {
    private Long reviewId;
    private Long userId;
    private Long animeId;
    private String animeTitle;
    private String reviewContent;
    private String nickname;
    private String createdAt;

    public static HomeRecentReviewItemDto animeTitleTranslationPick(HomeRecentReviewAnimeAllTitleItemDto dto) {
        String title = LocalizationUtil.pickTitle(
                dto.getTitleKor(),
                dto.getTitleEng(),
                dto.getTitleRom(),
                dto.getTitleNat()
        );
        return HomeRecentReviewItemDto.of(
                dto.getReviewId(),
                dto.getUserId(),
                dto.getAnimeId(),
                title,
                dto.getReviewContent(),
                dto.getNickname(),
                dto.getCreatedAt()
        );
    }
}
