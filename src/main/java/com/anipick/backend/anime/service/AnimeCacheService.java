package com.anipick.backend.anime.service;

import com.anipick.backend.anime.dto.AnimeAllTitleImgDto;
import com.anipick.backend.anime.dto.RangeDateRequestDto;
import com.anipick.backend.anime.mapper.AnimeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeCacheService {
    private final AnimeMapper animeMapper;

    @Cacheable(
            cacheNames = "upcomingSeasonAnimes",
            key = "#dto.startDate + ':' + #dto.endDate"
    )
    public List<AnimeAllTitleImgDto> getUpcomingSeasonAnimes(RangeDateRequestDto dto) {
        return animeMapper.selectUpcomingSeasonAnimes(dto);
    }
}
