package com.anipick.backend.explore.mapper;

import com.anipick.backend.explore.dto.ExploreAllTitleItemDto;
import com.anipick.backend.explore.dto.ExploreItemDto;
import com.anipick.backend.explore.dto.ExploreRequestDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExploreMapper {
    long countExplored(ExploreRequestDto exploreRequestDto);

    List<ExploreAllTitleItemDto> selectExplored(ExploreRequestDto exploreRequestDto);
}
