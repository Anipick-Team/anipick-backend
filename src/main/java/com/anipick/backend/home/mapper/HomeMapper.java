package com.anipick.backend.home.mapper;

import com.anipick.backend.anime.dto.ComingSoonItemAllTitleDto;
import com.anipick.backend.home.dto.HomeRecentReviewItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HomeMapper {
    List<HomeRecentReviewItemDto> selectHomeRecentReviews(
            @Param(value = "currentUserId") Long userId,
            @Param(value = "size") int size
    );

    List<ComingSoonItemAllTitleDto> selectHomeComingSoonAnimes(
            @Param(value = "defaultCoverUrl") String defaultCoverUrl,
            @Param(value = "orderByQuery") String orderByQuery,
            @Param(value = "size") int size
    );
}
