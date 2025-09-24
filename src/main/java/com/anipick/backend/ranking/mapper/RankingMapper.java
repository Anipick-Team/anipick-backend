package com.anipick.backend.ranking.mapper;

import com.anipick.backend.ranking.dto.AnimeGenresDto;
import com.anipick.backend.ranking.dto.RankingAnimesFromQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RankingMapper {
    List<RankingAnimesFromQueryDto> getYearSeasonRankingPaging(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("genreId") Long genreId,
            @Param("lastId") Long lastId,
            @Param("size") Integer size
    );

    List<AnimeGenresDto> getGenresByAnimeIds(
            @Param("animeIds") List<Long> animeIds
    );

    List<RankingAnimesFromQueryDto> getAllTimeRankingPaging(
            @Param("genreId") Long genreId,
            @Param("lastId") Long lastId,
            @Param("size") Integer size
    );
}
