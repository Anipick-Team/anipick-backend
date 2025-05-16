package com.anipick.backend.search.dto;

import java.util.List;

import com.anipick.backend.common.dto.CursorDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchStudioPageDto {
	private long count;
	private CursorDto cursor;
	private List<StudioItemDto> studios;
}
