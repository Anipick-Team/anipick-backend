package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyCommunityCommentDto {
    private Long commentId;
    private Long postId;
    private String animeTitle;
    private String animeCoverImageUrl;
    private String postTitle;
    private String content;
    private Long likeCount;
    private String createdAt;

    public static MyCommunityCommentDto seriesTitleTranslationPick(MyCommunityCommentAllTitleDto dto) {
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
                animeTitle,
                dto.getCoverImageUrl(),
                dto.getPostTitle(),
                dto.getContent(),
                dto.getLikeCount(),
                dto.getCreatedAt()
        );
    }
}
