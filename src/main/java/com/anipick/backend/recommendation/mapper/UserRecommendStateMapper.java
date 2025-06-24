package com.anipick.backend.recommendation.mapper;

import com.anipick.backend.recommendation.domain.UserRecommendMode;
import com.anipick.backend.recommendation.domain.UserRecommendState;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRecommendStateMapper {
    UserRecommendState findByUserId(@Param("userId") Long userId);

    void insertInitialState(
            @Param("userId") Long userId,
            @Param("mode") UserRecommendMode mode,
            @Param("referenceAnimeId") Long referenceAnimeId
    );

    void updateMode(
            @Param("userId") Long userId,
            @Param("mode") UserRecommendMode mode,
            @Param("referenceAnimeId") Long referenceAnimeId
    );
}
