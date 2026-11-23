package com.anipick.backend.community.mapper;

import com.anipick.backend.community.dto.LoungeSeriesRawDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityLoungeMapper {

    /**
     * 애니 ID 목록 → 소속 시리즈(게시판). 성인 애니와 시리즈 미매핑 애니는 결과에 없다.
     * series_anime.anime_id 가 UNIQUE 라 애니당 최대 1행.
     */
    List<LoungeSeriesRawDto> selectSeriesByAnimeIds(@Param("animeIds") List<Long> animeIds);
}
