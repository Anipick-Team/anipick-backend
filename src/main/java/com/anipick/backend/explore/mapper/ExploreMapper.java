package com.anipick.backend.explore.mapper;

import com.anipick.backend.explore.service.dto.ExploreItemDto;
import com.anipick.backend.explore.service.dto.ExploreRequestDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExploreMapper {
    long countExplored(ExploreRequestDto exploreRequestDto);

    List<ExploreItemDto> selectExplored(ExploreRequestDto exploreRequestDto);
}
