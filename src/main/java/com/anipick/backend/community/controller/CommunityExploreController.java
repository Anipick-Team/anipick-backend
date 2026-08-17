package com.anipick.backend.community.controller;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.community.dto.ExploreBoardPageDto;
import com.anipick.backend.community.service.CommunityExploreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community/explore")
@RequiredArgsConstructor
public class CommunityExploreController {

    private final CommunityExploreService communityExploreService;

    @GetMapping("/boards")
    public ApiResponse<ExploreBoardPageDto> getBoards(
            @RequestParam(value = "sort", defaultValue = "popular") String sort,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "lastValue", required = false) String lastValue,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        ExploreBoardPageDto result = communityExploreService.getBoards(sort, keyword, lastValue, lastId, size);
        return ApiResponse.success(result);
    }
}
