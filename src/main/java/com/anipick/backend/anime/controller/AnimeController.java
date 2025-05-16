package com.anipick.backend.anime.controller;

import com.anipick.backend.anime.dto.ComingSoonPageDto;
import com.anipick.backend.anime.dto.UpcomingSeasonResultDto;
import com.anipick.backend.anime.service.AnimeService;
import com.anipick.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/animes")
@RequiredArgsConstructor
public class AnimeController {

	private final AnimeService animeService;

	@GetMapping("/upcoming-season")
	public ApiResponse<UpcomingSeasonResultDto> getUpcomingSeasonAnimes() {
		// TODO: JWT 연동 후 실제 user parameter 추가
		UpcomingSeasonResultDto result = animeService.getUpcomingSeasonAnimes();
		return ApiResponse.success(result);
	}

	@GetMapping("/coming-soon")
	public ApiResponse<ComingSoonPageDto> getComingSoonAnimes(
			@RequestParam(value = "sort", defaultValue = "latest") String sort,
			@RequestParam(value = "lastId", required = false) Long lastId,
			@RequestParam(value = "size", defaultValue = "18") Long size,
			@RequestParam(value = "includeAdult", defaultValue = "0") Long includeAdult,
			@RequestParam(value = "lastValue", required = false) String lastValue
	) {
		// TODO: JWT 연동 후 실제 user parameter 추가
		ComingSoonPageDto result = animeService.getComingSoonAnimes(
				sort, lastId, size, includeAdult, lastValue
		);
		return ApiResponse.success(result);
	}
}