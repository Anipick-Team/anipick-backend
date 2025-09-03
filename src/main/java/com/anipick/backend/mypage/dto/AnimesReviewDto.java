package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimesReviewDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private Double rating;
    private Long reviewId;
    private String reviewContent;
    private String createdAt;
    private Long likeCount;
    private Boolean isLiked;

    public static AnimesReviewDto animeTitleTranslationPick(AnimesAllTitleReviewDto dto) {
        String title = LocalizationUtil.pickTitle(
				dto.getTitleKor(),
				dto.getTitleEng(),
				dto.getTitleRom(),
				dto.getTitleNat()
		);
        return new AnimesReviewDto(
                dto.getAnimeId(),
                title,
                dto.getCoverImageUrl(),
                dto.getRating(),
                dto.getReviewId(),
                dto.getReviewContent(),
                dto.getCreatedAt(),
                dto.getLikeCount(),
                dto.getIsLiked()
        );
    }
}
