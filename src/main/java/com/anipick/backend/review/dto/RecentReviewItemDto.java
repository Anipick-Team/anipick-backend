package com.anipick.backend.review.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RecentReviewItemDto {
    private Long reviewId;
    private Long userId;
    private Long animeId;
    private String animeTitle;
    private String animeCoverImageUrl;
    private Double rating;
    private String reviewContent;
    private String nickname;
    private String profileImageUrl;
    private String createdAt;
    private Long likeCount;
    private Boolean likedByCurrentUser;
    private Boolean isMine;

    public static RecentReviewItemDto animeTitleTranslationPick(RecentReviewItemAnimeAllTitleDto dto) {
        String title = LocalizationUtil.pickTitle(
				dto.getTitleKor(),
				dto.getTitleEng(),
				dto.getTitleRom(),
				dto.getTitleNat()
		);
        return new RecentReviewItemDto(
                dto.getReviewId(),
                dto.getUserId(),
                dto.getAnimeId(),
                title,
                dto.getAnimeCoverImageUrl(),
                dto.getRating(),
                dto.getReviewContent(),
                dto.getNickname(),
                dto.getProfileImageUrl(),
                dto.getCreatedAt(),
                dto.getLikeCount(),
                dto.getLikedByCurrentUser(),
                dto.getIsMine()
        );
    }
}
