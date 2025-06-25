package com.anipick.backend.recommendation.domain;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserState {
	private Long userId;
	private UserRecommendMode mode;
	private Long referenceAnimeId;
	private LocalDateTime startDate;
}
