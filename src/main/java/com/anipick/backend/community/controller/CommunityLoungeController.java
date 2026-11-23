package com.anipick.backend.community.controller;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.community.dto.LoungePageDto;
import com.anipick.backend.community.service.CommunityLoungeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community/lounge")
@RequiredArgsConstructor
public class CommunityLoungeController {

    private final CommunityLoungeService communityLoungeService;

    @GetMapping("/today")
    public ApiResponse<LoungePageDto> getTodayLounge(
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "size", defaultValue = "18") int size
    ) {
        LoungePageDto result = communityLoungeService.getTodayLounge(lastId, size);
        return ApiResponse.success(result);
    }
}
