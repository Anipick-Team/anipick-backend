package com.anipick.backend.ranking.mapper;

import com.anipick.backend.ranking.dto.AnimeGenresDto;
import com.anipick.backend.ranking.dto.RankingAnimesFromQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RankingMapper {
    List<RankingAnimesFromQueryDto> getYearSeasonRankingByGenre(
            @Param("year") Integer year,
            @Param("season") Integer season,
            @Param("genre") String genre,
            @Param("rankDate") LocalDate rankDate
    );

    List<RankingAnimesFromQueryDto> getYearSeasonRankingByGenrePaging(
            @Param("year") Integer year,
            @Param("season") Integer season,
            @Param("genre") String genre,
            @Param("lastId") Long lastId,
            @Param("size") Integer size,
            @Param("rankDate") LocalDate rankDate
    );

    List<RankingAnimesFromQueryDto> getYearSeasonRankingNotFilter(
            @Param("year") Integer year,
            @Param("season") Integer season,
            @Param("rankDate") LocalDate rankDate
    );

    List<RankingAnimesFromQueryDto> getYearSeasonRankingNotFilterPaging(
            @Param("year") Integer year,
            @Param("season") Integer season,
            @Param("lastId") Long lastId,
            @Param("size") Integer size,
            @Param("rankDate") LocalDate rankDate
    );

    List<AnimeGenresDto> getGenresByAnimeIds(
            @Param("animeIds") List<Long> animeIds
    );

    List<RankingAnimesFromQueryDto> getAllTimeRankingByGenre(
            @Param("genre") String genre,
            @Param("rankDate") LocalDate rankDate
    );

    List<RankingAnimesFromQueryDto> getAllTimeRankingByGenrePaging(
            @Param("genre") String genre,
            @Param("lastId") Long lastId,
            @Param("size") Integer size,
            @Param("rankDate") LocalDate rankDate
    );

    List<RankingAnimesFromQueryDto> getAllTimeRankingNotFilter(
            @Param("rankDate") LocalDate rankDate
    );

    List<RankingAnimesFromQueryDto> getAllTimeRankingNotFilterPaging(
            @Param("lastId") Long lastId,
            @Param("size") Integer size,
            @Param("rankDate") LocalDate rankDate
    );
}
