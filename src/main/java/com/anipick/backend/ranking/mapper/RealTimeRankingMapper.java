package com.anipick.backend.ranking.mapper;

import com.anipick.backend.ranking.dto.RealTimeRankingAnimesFromQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RealTimeRankingMapper {
    List<RealTimeRankingAnimesFromQueryDto> getRealTimeRanking();

    List<RealTimeRankingAnimesFromQueryDto> getRealTimeRankingPaging(
            @Param("lastValue") Long lastValue,
            @Param("lastId") Long lastId,
            @Param("size") Integer size
    );
}
