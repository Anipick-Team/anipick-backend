package com.anipick.backend.like.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikeController {

    private final LikeService likeService;


	@PostMapping("/animes/{animeId}/like")
	public ApiResponse<Void> likeAnime(
			@PathVariable(value = "animeId") Long animeId,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		Long userId = user.getUserId();
		likeService.likeAnime(userId, animeId);
		return ApiResponse.success();
	}
	@DeleteMapping("/animes/{animeId}/like")
	public ApiResponse<Void> notLikeAnime(
			@PathVariable(value = "animeId") Long animeId,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		Long userId = user.getUserId();
		likeService.notLikeAnime(userId, animeId);
		return ApiResponse.success();
	}

	@PostMapping("/persons/{personId}/like")
	public ApiResponse<Void> likePerson(
			@PathVariable(value = "personId") Long personId,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		Long userId = user.getUserId();
		likeService.likeActor(userId, personId);
		return ApiResponse.success();
	}

	@DeleteMapping("/persons/{personId}/like")
	public ApiResponse<Void> notLikePerson(
			@PathVariable(value = "personId") Long personId,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		Long userId = user.getUserId();
		likeService.notLikeActor(userId, personId);
		return ApiResponse.success();
	}
}
