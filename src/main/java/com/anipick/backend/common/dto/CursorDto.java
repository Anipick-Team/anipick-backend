package com.anipick.backend.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorDto {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String sort;
	private Long lastId;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String lastValue;

	public CursorDto(Long lastId) {
		this.lastId = lastId;
	}

	public CursorDto(String sort, Long lastId) {
		this.sort = sort;
		this.lastId = lastId;
	}

	public static CursorDto of(Long lastId) {
		return new CursorDto(lastId);
	}

	public static CursorDto of(String sort, Long lastId) {
		return new CursorDto(sort, lastId);
	}

	public static CursorDto of(String sort, Long lastId, String lastValue) {
		return new CursorDto(sort, lastId, lastValue);
	}
}
