package com.anipick.backend.explore.mapper;

import com.anipick.backend.explore.dto.ExploreItemDto;
import com.anipick.backend.explore.dto.ExploreRequestDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExploreMapper {
    long countExplored(ExploreRequestDto exploreRequestDto);

    List<ExploreItemDto> selectExplored(ExploreRequestDto exploreRequestDto);

    List<ExploreItemDto> selectExploredTest(ExploreRequestDto exploreRequestDto);

    List<ExploreItemDto> selectExploredSemiJoin(ExploreRequestDto exploreRequestDto);

    List<ExploreItemDto> selectExploredBestFast(ExploreRequestDto exploreRequestDto);
}
