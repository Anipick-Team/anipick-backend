package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyCommunityPostDto {
    private Long postId;
    private Long seriesId;
    private String animeTitle;
    private String animeCoverImageUrl;
    private String title;
    private String content;
    private Long thumbnailImageId;
    private Boolean isSpoiler;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String createdAt;

    public static MyCommunityPostDto seriesTitleTranslationPick(MyCommunityPostAllTitleDto dto) {
        String animeTitle = LocalizationUtil.pickTitle(
                null,
                dto.getTitleKor(),
                dto.getTitleEng(),
                dto.getTitleRom(),
                dto.getTitleNat()
        );
        return new MyCommunityPostDto(
                dto.getPostId(),
                dto.getSeriesId(),
                animeTitle,
                dto.getCoverImageUrl(),
                dto.getTitle(),
                dto.getContent(),
                dto.getThumbnailImageId(),
                dto.getIsSpoiler(),
                dto.getViewCount(),
                dto.getLikeCount(),
                dto.getCommentCount(),
                dto.getCreatedAt()
        );
    }
}
