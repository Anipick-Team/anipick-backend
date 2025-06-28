package com.anipick.backend.studio.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.studio.dto.StudioDetailPageDto;
import com.anipick.backend.studio.service.StudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/studios")
@RequiredArgsConstructor
public class StudioController {

    private final StudioService studioService;

    @GetMapping("/{studioId}/animes")
    public ApiResponse<StudioDetailPageDto> getStudioAnimes(
            @PathVariable(value = "studioId") Long studioId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "lastValue", required = false) Long lastValue,
            @RequestParam(value = "size", defaultValue = "18") int size
    ) {
        StudioDetailPageDto result = studioService.getStudioAnimes(studioId, lastId, lastValue, size);
        return ApiResponse.success(result);
    }
}
