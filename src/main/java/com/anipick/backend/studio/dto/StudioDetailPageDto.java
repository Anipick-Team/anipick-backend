package com.anipick.backend.studio.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class StudioDetailPageDto {
    private String studioName;
    private CursorDto cursor;
    private List<StudioAnimeItemStringYearDto> animes;
}
