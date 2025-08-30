package com.anipick.backend.mypage.mapper;

import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.mypage.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyPageMapper {
    Long getMyWatchCount(@Param("userId") Long userId, @Param("animeStatus") String animeStatus);

    List<LikedAnimesAllTitleDto> getMyLikedAnimes(
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("size") Integer size
    );

    List<LikedPersonsDto> getMyLikedPersons(
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("size") Integer size
    );

    List<WatchListAnimesAllTitleDto> getMyWatchListAnimes(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("lastId") Long lastId,
            @Param("size") Integer size
    );

    List<WatchingAnimesAllTitleDto> getMyWatchingAnimes(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("lastId") Long lastId,
            @Param("size") Integer size
    );

    List<FinishedAnimesAllTitleDto> getMyFinishedAnimes(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("lastId") Long lastId,
            @Param("size") Integer size
    );

    Long getMyReviewCount(@Param("userId") Long userId);

    List<AnimesAllTitleReviewDto> getMyAnimesReviewsAll( // 리뷰만 보기 off
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("size") Integer size,
            @Param("sortOption") String sortOption,
            @Param("lastLikeCount") Long lastLikeCount,
            @Param("lastRating") Double lastRating
    );

    List<AnimesAllTitleReviewDto> getMyAnimesReviewsOnly( // 리뷰만 보기 on
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("size") Integer size,
            @Param("sortOption") String sortOption,
            @Param("lastLikeCount") Long lastLikeCount,
            @Param("lastRating") Double lastRating
    );

    Long getMyAnimesLikeCount(@Param("userId") Long userId);

    Long getMyPersonsLikeCount(@Param("userId") Long userId);
}
