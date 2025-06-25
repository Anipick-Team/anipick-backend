package com.anipick.backend.anime.mapper;

import com.anipick.backend.search.dto.StudioItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudioMapper {
    List<StudioItemDto> selectStudiosByAnimeId(@Param(value = "animeId") Long animeId);
}
