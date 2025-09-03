package com.anipick.backend.anime.mapper;

import com.anipick.backend.anime.dto.StudioAllNameItemDto;
import com.anipick.backend.studio.dto.StudioAnimeAllTitleItemDto;
import com.anipick.backend.studio.dto.StudioName;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudioMapper {
    List<StudioAllNameItemDto> selectStudiosByAnimeId(@Param(value = "animeId") Long animeId);

    StudioName selectStudioNameByStudioId(@Param(value = "studioId") Long studioId);

    List<StudioAnimeAllTitleItemDto> selectAnimesOfStudio(
            @Param(value = "studioId") Long studioId,
            @Param(value = "lastId") Long lastId,
            @Param(value = "lastValue") Long lastValue,
            @Param(value = "size") int size
    );
}
