package com.anipick.backend.studio.service;

import com.anipick.backend.anime.mapper.StudioMapper;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.util.LocalizationUtil;
import com.anipick.backend.studio.dto.StudioAnimeItemDto;
import com.anipick.backend.studio.dto.StudioAnimeItemStringYearDto;
import com.anipick.backend.studio.dto.StudioDetailPageDto;
import com.anipick.backend.studio.dto.StudioName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudioService {

    private final StudioMapper studioMapper;


    public StudioDetailPageDto getStudioAnimes(Long studioId, Long lastId, Long lastValue, int size) {
        StudioName studioNameKorAndEng = studioMapper.selectStudioNameByStudioId(studioId);

        String studioName = LocalizationUtil.pickStudioName(studioNameKorAndEng.getNameKor(), studioNameKorAndEng.getNameEng());

        List<StudioAnimeItemDto> items = studioMapper.selectAnimesOfStudio(studioId, lastId, lastValue, size)
                .stream()
                .map(StudioAnimeItemDto::animeTitleTranslationPick)
                .toList();

        Long nextId;
        Long nextValue;

        if (items.isEmpty()) {
            nextId = null;
            nextValue = null;
        } else {
            nextId = items.getLast().getAnimeId();
            nextValue = items.getLast().getSeasonYear();
        }

        List<StudioAnimeItemStringYearDto> convertYearItems = items.stream()
                .map(StudioAnimeItemStringYearDto::from)
                .toList();
        String nextValueStr;

        if (nextValue == null) {
            nextValueStr = null;
        } else {
            nextValueStr = nextValue.toString();
        }

        CursorDto cursor = CursorDto.of(null, nextId, nextValueStr);

        return StudioDetailPageDto.of(studioName, cursor, convertYearItems);
    }
}
