package com.anipick.backend.mypage.dto;

import com.anipick.backend.anime.dto.GenreDto;
import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyCommunityCommentDto {
    private Long commentId;
    private Long postId;
    private Long seriesId;
    private String animeTitle;
    private String animeCoverImageUrl;
    private List<GenreDto> genres;
    private String postTitle;
    private String content;
    private Long likeCount;
    private String createdAt;

    public static MyCommunityCommentDto seriesTitleTranslationPick(MyCommunityCommentAllTitleDto dto, List<GenreDto> genres) {
        String animeTitle = LocalizationUtil.pickTitle(
                null,
                dto.getTitleKor(),
                dto.getTitleEng(),
                dto.getTitleRom(),
                dto.getTitleNat()
        );
        return new MyCommunityCommentDto(
                dto.getCommentId(),
                dto.getPostId(),
                dto.getSeriesId(),
                animeTitle,
                dto.getCoverImageUrl(),
                genres.stream()
                        .limit(3)
                        .toList(),
                dto.getPostTitle(),
                dto.getContent(),
                dto.getLikeCount(),
                dto.getCreatedAt()
        );
    }
}
