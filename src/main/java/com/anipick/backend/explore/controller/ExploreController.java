package com.anipick.backend.explore.controller;

import java.util.List;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.util.LastValueTypeConverter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
		@RequestParam(value = "lastValue", required = false) String lastValue,
		@RequestParam(value = "size", defaultValue = "18") int size,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		if (year == null && season != null) {
			return ApiResponse.error(ErrorCode.EMPTY_YEAR);
		}
		Double parsedIntegerLastValue = LastValueTypeConverter.toDouble(lastValue);
		ExplorePageDto page = exploreService.explore(
			year, season, genres, genreOp, type, sort, lastId, parsedIntegerLastValue, size
		);
		return ApiResponse.success(page);
	}
}