package com.anipick.backend.anime.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnimeStatus {
	/**
	 * 완료되어 출시되지 않는 상태
	 */
	FINISHED("방영 종료"),
	/**
	 * 현재 출시 중인 상태
	 */
	RELEASING("방영 중"),
	/**
	 * 추후 공개 예정인 상태
	 */
	NOT_YET_RELEASED("방영 예정"),
	/**
	 * 작업이 완료되기 전에 끝난 상태
	 */
	CANCELLED("방영 종료"),
	/**
	 * 현재 출시가 일시 중단되었으며 추후 재개될 상태
	 */
	HIATUS("일시 중단");

	private final String statusName;
}
