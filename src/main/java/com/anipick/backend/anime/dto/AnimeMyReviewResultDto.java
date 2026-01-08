package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeMyReviewResultDto {
	private Long reviewId;
	private Double rating;
	private String content;
	private String createdAt;
	private Long likeCount;
	private Boolean isLiked;

	public static AnimeMyReviewResultDto createdAtFormatted(AnimeMyReviewResultDto dto, String formatCreatedAtStr) {
		return new AnimeMyReviewResultDto(
			dto.getReviewId(),
			dto.getRating(),
			dto.getContent(),
			formatCreatedAtStr,
			dto.getLikeCount(),
			dto.getIsLiked()
		);
	}

	public static AnimeMyReviewResultDto createdAtOnlyRatingFormatted(AnimeMyReviewResultDto dto, String formatCreatedAtStr) {
		return new AnimeMyReviewResultDto(
			dto.getReviewId(),
			dto.getRating(),
			dto.getContent(),
			formatCreatedAtStr,
			dto.getLikeCount(),
			null
		);
	}

	public static AnimeMyReviewResultDto empty() {
		return new AnimeMyReviewResultDto(null, null, null, null, null, null);
	}
}
