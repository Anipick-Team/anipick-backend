package com.anipick.backend.anime.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnimeStatus {
	FINISHED, RELEASING, NOT_YET_RELEASED, CANCELLED, HIATUS
}
