package com.anipick.backend.common.domain;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortOption {
	LATEST("latest", "a.anime_id DESC"),
	POPULARITY("popularity", "a.popularity DESC"),
	START_DATE("startDate", "a.start_date ASC"),
	RATING_DESC("ratingDesc", "r.rating DESC"),
	RATING_ASC("ratingAsc", "r.rating ASC"),
	LIKES("likes", "r.like_count DESC"),
	RATING("rating", "a.review_average_score DESC");

	private final String code;
	private final String orderByQuery;

	public static SortOption of(String code) {
		return switch (code) {
			case "latest" -> LATEST;
			case "popularity" -> POPULARITY;
			case "startDate" -> START_DATE;
			case "ratingDesc" -> RATING_DESC;
			case "ratingAsc" -> RATING_ASC;
			case "likes" -> LIKES;
			case "rating" -> RATING;
			default -> throw new CustomException(ErrorCode.BAD_REQUEST);
		};
	}
}
