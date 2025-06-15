package com.anipick.backend.ranking.mapper;

import com.anipick.backend.ranking.dto.RankingAnimesDto;
import com.anipick.backend.ranking.dto.RankingAnimesFromQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RankingMapper {
    List<RankingAnimesFromQueryDto> getYearSeasonRankingFromNotFilter(
            @Param("year") Integer year,
            @Param("season") Integer season,
            @Param("rankDate") LocalDate rankDate
    );

    List<RankingAnimesFromQueryDto> getYearSeasonRanking(
            @Param("year") Integer year,
            @Param("season") Integer season,
            @Param("genre") String genre,
            @Param("rankDate") LocalDate rankDate
    );

    List<RankingAnimesFromQueryDto> getYearSeasonRankingPaging(
            @Param("year") Integer year,
            @Param("season") Integer season,
            @Param("genre") String genre,
            @Param("lastId") Long lastId,
            @Param("size") Integer size,
            @Param("rankDate") LocalDate rankDate
    );

    List<RankingAnimesFromQueryDto> getAllTimeRanking(
            @Param("genre") String genre,
            @Param("rankDate") LocalDate rankDate
    );

    List<RankingAnimesFromQueryDto> getAllTimeRankingPaging(
            @Param("genre") String genre,
            @Param("lastId") Long lastId,
            @Param("size") Integer size,
            @Param("rankDate") LocalDate rankDate
    );
}
