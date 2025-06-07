package com.anipick.backend.recommendation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.anipick.backend.recommendation.domain.UserRecommendMode;
import com.anipick.backend.recommendation.domain.UserState;

@Mapper
public interface UserStateMapper {
	UserState findByUserId(@Param("userId") Long userId);

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

	void updateReferenceAnime(
		@Param("userId") Long userId,
		@Param("referenceAnimeId") Long referenceAnimeId
	);
}