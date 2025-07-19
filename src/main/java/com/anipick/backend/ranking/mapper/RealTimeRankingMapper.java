package com.anipick.backend.ranking.mapper;

import com.anipick.backend.ranking.dto.RealTimeRankingAnimesFromQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RealTimeRankingMapper {
    List<RealTimeRankingAnimesFromQueryDto> getRealTimeRanking(
            @Param("animeIds") List<Long> animeIds,
            @Param("genre") String genre
    );
}
