package com.anipick.backend.community.mapper;

import com.anipick.backend.community.dto.ExploreBoardRawDto;
import com.anipick.backend.community.dto.SeriesGenreRawDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityExploreMapper {

    /**
     * 탐색 대상 시리즈 전체 수 (keyword 있으면 제목 LIKE 필터 적용).
     */
    Long countExploreBoards(@Param("keyword") String keyword);

    /**
     * 인기순: 실시간 랭킹 기준(시리즈 소속 애니의 MAX(trending) DESC, MAX(popularity) DESC), series_id DESC 커서.
     * 소속 애니가 없는 시리즈는 0으로 집계되어 뒤에 온다.
     */
    List<ExploreBoardRawDto> selectPopularBoards(
            @Param("keyword") String keyword,
            @Param("lastTrending") Long lastTrending,
            @Param("lastPopularity") Long lastPopularity,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );

    /**
     * 최신순: 가장 최근 활성 게시글 시각 DESC, series_id DESC 커서.
     * 게시글 없는 시리즈(lastPostedAt IS NULL)는 맨 뒤 구간으로 이어진다.
     * lastValue 가 null 이고 lastId 만 있으면 NULL 구간 내 페이징.
     */
    List<ExploreBoardRawDto> selectLatestBoards(
            @Param("keyword") String keyword,
            @Param("lastValue") String lastValue,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );

    /**
     * 시리즈별 대표작(sort_order 최소) 애니의 장르 벌크 조회.
     */
    List<SeriesGenreRawDto> selectRepresentativeGenresBySeriesIds(
            @Param("seriesIds") List<Long> seriesIds
    );
}
