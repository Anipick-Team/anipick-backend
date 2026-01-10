package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@Builder
public class RangeDateRequestDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String startDate;
	private String endDate;
}
