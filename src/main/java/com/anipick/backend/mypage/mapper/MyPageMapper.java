package com.anipick.backend.mypage.mapper;

import com.anipick.backend.mypage.dto.LikedAnimesDto;
import com.anipick.backend.mypage.dto.LikedPersonsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyPageMapper {
    Long getMyWatchCount(@Param("userId") Long userId, @Param("animeStatus") String animeStatus);

    List<LikedAnimesDto> getMyLikedAnimes(@Param("userId") Long userId, @Param("size") Integer size);

    List<LikedPersonsDto> getMyLikedPersons(@Param("userId") Long userId, @Param("size") Integer size);
}
