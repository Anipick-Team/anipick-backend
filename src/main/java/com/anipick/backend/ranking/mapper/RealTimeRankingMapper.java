package com.anipick.backend.ranking.mapper;

import com.anipick.backend.ranking.dto.RankingAnimesFromQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RealTimeRankingMapper {
    List<RankingAnimesFromQueryDto> getRealTimeRanking(
            @Param("genre") String genre,
            @Param("rankDateTime") String rankDateTime
    );

    List<RankingAnimesFromQueryDto> getRealTimeRankingPaging(
            @Param("genre") String genre,
            @Param("lastId") Long lastId,
            @Param("size") Integer size,
            @Param("rankDateTime") String rankDateTime
    );
}
