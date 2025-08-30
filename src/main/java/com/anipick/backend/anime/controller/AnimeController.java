package com.anipick.backend.anime.controller;


import com.anipick.backend.anime.domain.AnimeCharacterRole;
import com.anipick.backend.anime.dto.*;
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
			@RequestParam(value = "includeAdult", defaultValue = "false") Boolean includeAdult,
			@RequestParam(value = "lastValue", required = false) String lastValue,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		ComingSoonPageDto result = animeService.getComingSoonAnimes(
				sort, lastId, size, includeAdult, lastValue
		);
		return ApiResponse.success(result);
	}

	@GetMapping("/{animeId}/reviews")
	public ApiResponse<AnimeDetailInfoReviewsPageDto> getAnimeInfoReviews(
			@PathVariable(value = "animeId") Long animeId,
			@RequestParam(value = "sort", defaultValue = "latest") String sort,
			@RequestParam(value = "lastId", required = false) Long lastId,
			@RequestParam(value = "lastValue", required = false) String lastValue,
			@RequestParam(value = "size", defaultValue = "20") int size,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		Long userId = user.getUserId();
		AnimeDetailInfoReviewsPageDto result = animeService.getAnimeInfoReviews(
				animeId, userId, sort, lastId, lastValue, size
		);
		return ApiResponse.success(result);
	}

	@GetMapping("/{animeId}/detail/info")
	public ApiResponse<AnimeDetailInfoResultDto> getAnimeInfoDetail(
			@PathVariable(value = "animeId") Long animeId,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		  Long userId = user.getUserId();
		  AnimeDetailInfoResultDto result = animeService.getAnimeInfoDetail(animeId, userId);
      return ApiResponse.success(result);
	}
  
	@GetMapping("/{animeId}/detail/recommendation")
	public ApiResponse<List<AnimeItemDto>> getAnimeDetailRecommendation(
			@PathVariable(value = "animeId") Long animeId,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		  List<AnimeItemDto> result = animeService.getAnimeRecommendation(animeId);
  		return ApiResponse.success(result);
	}  
  
	@GetMapping("/{animeId}/detail/series")
	public ApiResponse<List<AnimeSeriesItemResultDto>> getAnimeDetailSeries(
			@PathVariable(value = "animeId") Long animeId,
			@AuthenticationPrincipal CustomUserDetails user
	) {
	    List<AnimeSeriesItemResultDto> result = animeService.getAnimeSeries(animeId);
  		return ApiResponse.success(result);
	}
  
	@GetMapping("/{animeId}/detail/actor")
	public ApiResponse<List<AnimeCharacterActorItemPickNameDto>> getAnimeInfoCharacterActor(
			@PathVariable(value = "animeId") Long animeId,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		List<AnimeCharacterActorItemPickNameDto> result = animeService.getAnimeInfoCharacterActor(animeId);
		return ApiResponse.success(result);
	}

	@GetMapping("/{animeId}/characters")
    public ApiResponse<AnimeCharacterActorPageDto> getAnimeCharacterActor(
        @PathVariable(value = "animeId") Long animeId,
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestParam(value = "lastId", required = false) Long lastId,
        @RequestParam(value = "lastValue", required = false) AnimeCharacterRole lastValue,
        @RequestParam(value = "size", defaultValue = "18") int size
    ) {
        AnimeCharacterActorPageDto result = animeService.getAnimeCharacterActor(animeId, lastId, lastValue, size);
        return ApiResponse.success(result);
    }

	@GetMapping("/{animeId}/recommendations")
	public ApiResponse<AnimeRecommendationPageDto> getAnimeRecommendations(
			@PathVariable(value = "animeId") Long animeId,
			@AuthenticationPrincipal CustomUserDetails user,
			@RequestParam(value = "lastId", required = false) Long lastId,
			@RequestParam(value = "size", defaultValue = "18") int size
	) {
		AnimeRecommendationPageDto result = animeService.getRecommendationsByAnime(animeId, lastId, size);
		return ApiResponse.success(result);
	}

	@GetMapping("/{animeId}/series")
	public ApiResponse<AnimeSeriesPageDto> getAnimeSeries(
			@PathVariable(value = "animeId") Long animeId,
			@AuthenticationPrincipal CustomUserDetails user,
			@RequestParam(value = "lastId", required = false) Long lastId,
			@RequestParam(value = "size", defaultValue = "18") int size
	) {
		AnimeSeriesPageDto result = animeService.getSeriesByAnime(animeId, lastId, size);
		return ApiResponse.success(result);
	}

	@GetMapping("/{animeId}/my-review")
	public ApiResponse<AnimeMyReviewResultDto> getAnimeMyReview(
		@PathVariable(value = "animeId") Long animeId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		Long userId = user.getUserId();
		AnimeMyReviewResultDto result = animeService.getAnimeMyReview(animeId, userId);
		return ApiResponse.success(result);
	}
}