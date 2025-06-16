package com.anipick.backend.anime.controller;

import com.anipick.backend.anime.dto.AnimeCharacterActorItemDto;
import com.anipick.backend.anime.dto.ComingSoonPageDto;
import com.anipick.backend.anime.dto.UpcomingSeasonResultDto;
import com.anipick.backend.anime.service.AnimeService;
import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	@GetMapping("/{animeId}/detail/actor")
	public ApiResponse<List<AnimeCharacterActorItemDto>> getAnimeInfoCharacterActor(
			@PathVariable(value = "animeId") Long animeId,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		List<AnimeCharacterActorItemDto> result = animeService.getAnimeInfoCharacterActor(animeId);
		return ApiResponse.success(result);
	}
}