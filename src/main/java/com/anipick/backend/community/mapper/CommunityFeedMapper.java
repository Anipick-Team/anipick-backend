package com.anipick.backend.community.mapper;

import com.anipick.backend.community.dto.FeedPostRawDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CommunityFeedMapper {

    /**
     * 전체 게시글 피드. 작성순(post_id DESC) 커서.
     */
    List<FeedPostRawDto> selectAllFeedPosts(
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );

    /**
     * 내가 시청중(status)으로 둔 애니가 속한 시리즈의 게시글 피드. 작성순(post_id DESC) 커서.
     */
    List<FeedPostRawDto> selectWatchingFeedPosts(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );

    /**
     * 실시간 인기 피드. periodStart 이후 좋아요가 1개 이상인 글만, (기간 내 좋아요 수 DESC, post_id DESC) 커서.
     */
    List<FeedPostRawDto> selectPopularFeedPosts(
            @Param("userId") Long userId,
            @Param("periodStart") LocalDateTime periodStart,
            @Param("lastValue") Long lastValue,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );
}
