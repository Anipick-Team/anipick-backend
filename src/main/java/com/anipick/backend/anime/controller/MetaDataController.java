package com.anipick.backend.anime.controller;

import com.anipick.backend.anime.dto.MetaDataGroupResult;
import com.anipick.backend.anime.service.MetaDataService;
import com.anipick.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/animes")
@RequiredArgsConstructor
public class MetaDataController {

    private final MetaDataService service;

    @GetMapping("/meta-data-group")
    public ApiResponse<MetaDataGroupResult> getMetaDataGroup() {
        MetaDataGroupResult result = service.getMetaDataGroup();
        return ApiResponse.success(result);
    }
}
