package com.anipick.backend.recommendation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewUserMapper {
	Long findMostRecentHighRatedAnime(@Param("userId") Long userId);

	List<Long> findTopRatedAnimeIds(@Param("userId") Long userId, @Param("limit")  int limit);

	List<Long> findUserReviews(@Param("userId") Long userId);

	long countReviewsByUser(@Param("userId") Long userId);
}
