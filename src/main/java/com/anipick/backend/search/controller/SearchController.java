package com.anipick.backend.search.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.search.dto.SearchInitPageDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.search.dto.SearchAnimePageDto;
import com.anipick.backend.search.dto.SearchPersonPageDto;
import com.anipick.backend.search.dto.SearchStudioPageDto;
import com.anipick.backend.search.service.SearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
	private final SearchService searchService;

	@GetMapping("/init")
	public ApiResponse<SearchInitPageDto> findWeekBestAnime(
			@AuthenticationPrincipal CustomUserDetails user
	) {
		SearchInitPageDto weekBestAnimes = searchService.findWeekBestAnimes();
		return ApiResponse.success(weekBestAnimes);
	}

	@GetMapping("/animes")
	public ApiResponse<SearchAnimePageDto> findSearchAnimes(
		@RequestParam(value = "query", required = false) String query,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		if (query == null || query.isBlank()) {
			throw new CustomException(ErrorCode.EMPTY_KEYWORD);
		}
		SearchAnimePageDto searchAnimes = searchService.findSearchAnimes(query, lastId, size);
		return ApiResponse.success(searchAnimes);
	}

	@GetMapping("/persons")
	public ApiResponse<SearchPersonPageDto> findSearchPersons(
		@RequestParam(value = "query", required = false) String query,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size,
		@AuthenticationPrincipal CustomUserDetails user

	) {
		if (query == null || query.isBlank()) {
			throw new CustomException(ErrorCode.EMPTY_KEYWORD);
		}
		SearchPersonPageDto searchPersons = searchService.findSearchPersons(query, lastId, size);
		return ApiResponse.success(searchPersons);
	}

	@GetMapping("/studios")
	public ApiResponse<SearchStudioPageDto> findSearchStudios(
		@RequestParam(value = "query", required = false) String query,
		@RequestParam(value = "lastId", required = false) Long lastId,
		@RequestParam(value = "size", defaultValue = "18") Long size,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		if (query == null || query.isBlank()) {
			throw new CustomException(ErrorCode.EMPTY_KEYWORD);
		}
		SearchStudioPageDto searchStudios = searchService.findSearchStudios(query, lastId, size);
		return ApiResponse.success(searchStudios);
	}
}
