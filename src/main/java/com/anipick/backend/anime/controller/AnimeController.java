package com.anipick.backend.anime.controller;

import com.anipick.backend.anime.dto.ComingSoonPageDto;
import com.anipick.backend.anime.dto.UpcomingSeasonResultDto;
import com.anipick.backend.anime.service.AnimeService;
import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.anime.dto.AnimeDetailInfoReviewsPageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animes")
@RequiredArgsConstructor
public class AnimeController {

	private final AnimeService animeService;

	@GetMapping("/upcoming-season")
	public ApiResponse<UpcomingSeasonResultDto> getUpcomingSeasonAnimes(
			@AuthenticationPrincipal CustomUserDetails user
			) {
		UpcomingSeasonResultDto result = animeService.getUpcomingSeasonAnimes();
		return ApiResponse.success(result);
	}

	@GetMapping("/coming-soon")
	public ApiResponse<ComingSoonPageDto> getComingSoonAnimes(
			@RequestParam(value = "sort", defaultValue = "latest") String sort,
			@RequestParam(value = "lastId", required = false) Long lastId,
			@RequestParam(value = "size", defaultValue = "18") Long size,
			@RequestParam(value = "includeAdult", defaultValue = "0") Long includeAdult,
			@RequestParam(value = "lastValue", required = false) String lastValue,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		ComingSoonPageDto result = animeService.getComingSoonAnimes(
				sort, lastId, size, includeAdult, lastValue
		);
		return ApiResponse.success(result);
	}

	@GetMapping("/{animeId}/reviews")
	public ApiResponse<?> getAnimeInfoReviews(
			@PathVariable(value = "animeId") Long animeId,
			@RequestParam(value = "sort", defaultValue = "latest") String sort,
			@RequestParam(value = "isSpoiler", defaultValue = "false") Boolean isSpoiler,
			@RequestParam(value = "lastId", required = false) Long lastId,
			@RequestParam(value = "lastValue", required = false) String lastValue,
			@RequestParam(value = "size", defaultValue = "20") int size,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		Long userId = user.getUserId();
		AnimeDetailInfoReviewsPageDto result = animeService.getAnimeInfoReviews(
				animeId, userId, sort, isSpoiler, lastId, lastValue, size
		);
		return ApiResponse.success(result);
	}

}