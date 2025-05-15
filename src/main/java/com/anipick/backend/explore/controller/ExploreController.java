package com.anipick.backend.explore.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.explore.domain.GenresOption;
import com.anipick.backend.explore.dto.ExplorePageDto;
import com.anipick.backend.explore.service.ExploreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/explore/animes")
@RequiredArgsConstructor
public class ExploreController {
	private final ExploreService exploreService;

	@GetMapping
	public ApiResponse<ExplorePageDto> explore(
		@RequestParam(value = "year", required = false) Integer year,
		@RequestParam(value = "season", required = false) Integer season,
		@RequestParam(value = "genres", required = false) List<Long> genres,
		@RequestParam(value = "genreOp", defaultValue = "OR") GenresOption genreOp,
		@RequestParam(value = "type", required = false) String type,
		@RequestParam(value = "sort", defaultValue = "popularity") String sort,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "lastValue", required = false) Integer lastValue,
		@RequestParam(value = "size", defaultValue = "18") int size
	) {
		// TODO: JWT 연동 후 실제 user parameter 추가
		if (year == null && season != null) {
			return ApiResponse.error(ErrorCode.EMPTY_YEAR);
		}
		ExplorePageDto page = exploreService.explore(
			year, season, genres, genreOp, type, sort, lastId, lastValue, size
		);
		return ApiResponse.success(page);
	}
}