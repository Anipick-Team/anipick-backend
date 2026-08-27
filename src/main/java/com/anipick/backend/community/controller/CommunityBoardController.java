package com.anipick.backend.community.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.community.dto.BoardByAnimeResultDto;
import com.anipick.backend.community.dto.PostListPageDto;
import com.anipick.backend.community.service.CommunityBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/boards")
@RequiredArgsConstructor
public class CommunityBoardController {

    private final CommunityBoardService communityBoardService;

    @GetMapping("/by-anime/{animeId}")
    public ApiResponse<BoardByAnimeResultDto> getBoardByAnime(
            @PathVariable(name = "animeId") Long animeId
    ) {
        BoardByAnimeResultDto result = communityBoardService.getBoardByAnime(animeId);
        return ApiResponse.success(result);
    }

    @GetMapping("/{seriesId}/posts")
    public ApiResponse<PostListPageDto> getPosts(
            @PathVariable(name = "seriesId") Long seriesId,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            @RequestParam(value = "lastValue", required = false) String lastValue,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        PostListPageDto result = communityBoardService.getPosts(seriesId, sort, lastValue, lastId, size, userId);
        return ApiResponse.success(result);
    }
}
