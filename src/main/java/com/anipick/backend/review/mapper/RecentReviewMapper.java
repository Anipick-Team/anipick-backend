package com.anipick.backend.review.mapper;

import com.anipick.backend.review.dto.RecentReviewItemAnimeAllTitleDto;
import com.anipick.backend.review.dto.RecentReviewItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecentReviewMapper {

    long countRecentReviews(@Param("currentUserId") Long currentUserId);

    List<RecentReviewItemAnimeAllTitleDto> selectRecentReviews(
            @Param("currentUserId") Long currentUserId,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );
}
