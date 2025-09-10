package com.anipick.backend.explore.mapper;

import com.anipick.backend.explore.dto.AnimeIdGenreDto;
import com.anipick.backend.explore.dto.SignUpAnimeExploreSearchRequestDto;
import com.anipick.backend.explore.dto.SignUpPopularAnimeAllTitleItemDto;
import com.anipick.backend.explore.dto.SignUpPopularAnimeItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserSignUpAnimeMapper {
    long countExploredAndSearch(SignUpAnimeExploreSearchRequestDto dto);

    List<SignUpPopularAnimeAllTitleItemDto> selectAnimeExploredAndSearch(SignUpAnimeExploreSearchRequestDto dto);

    List<AnimeIdGenreDto> selectGenresByAnimeIds(@Param(value = "animeIds") List<Long> animeIds);
}
