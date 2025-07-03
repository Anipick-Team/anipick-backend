package com.anipick.backend.explore.controller;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.explore.dto.UserSignUpAnimePageDto;
import com.anipick.backend.explore.service.UserSignUpAnimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SignUpAnimeController {

    private final UserSignUpAnimeService userSignUpAnimeService;

    @GetMapping("/explore-search")
    public ApiResponse<UserSignUpAnimePageDto> getSignUpAnimeExploreSearch(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "season", required = false) Integer season,
            @RequestParam(value = "genres", required = false) Long genres,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        if (year == null && season != null) {
			return ApiResponse.error(ErrorCode.EMPTY_YEAR);
		}
        UserSignUpAnimePageDto result =
                userSignUpAnimeService.getAnimeExploreSearch(query, year, season, genres, lastId, size);
        return ApiResponse.success(result);
    }
}
