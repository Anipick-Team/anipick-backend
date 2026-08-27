package com.anipick.backend.community.mapper;

import com.anipick.backend.anime.dto.GenreDto;
import com.anipick.backend.community.dto.BoardInfoRawDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityBoardMapper {

    /**
     * 애니 ID 로 매핑된 시리즈(게시판) 조회. 매핑이 없으면 null.
     */
    BoardInfoRawDto selectBoardByAnimeId(@Param("animeId") Long animeId);

    /**
     * 시리즈 대표작(sort_order 최소) 애니의 장르 목록.
     */
    List<GenreDto> selectRepresentativeGenresBySeriesId(@Param("seriesId") Long seriesId);

    /**
     * 게시판(시리즈)이 없을 때 팝업용으로 쓰는 애니 자체 장르 목록.
     */
    List<GenreDto> selectGenresByAnimeId(@Param("animeId") Long animeId);

    /**
     * 게시판의 활성 게시글 수(soft delete 제외).
     */
    Long countPostsBySeriesId(@Param("seriesId") Long seriesId);

    boolean existsSeries(@Param("seriesId") Long seriesId);
}
