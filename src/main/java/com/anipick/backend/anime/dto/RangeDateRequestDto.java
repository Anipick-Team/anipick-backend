package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RangeDateRequestDto {
	private String startDate;
	private String endDate;
}
