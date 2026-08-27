package com.anipick.backend.community.mapper;

import com.anipick.backend.community.domain.CommunityPost;
import com.anipick.backend.community.dto.PostCreateRequest;
import com.anipick.backend.community.dto.PostDetailRawDto;
import com.anipick.backend.community.dto.PostListItemDto;
import com.anipick.backend.community.dto.PostUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CommunityPostMapper {

    /**
     * 게시판 전체 게시글 수(차단 유저 글 제외, soft delete 제외).
     */
    Long countBoardPosts(
            @Param("seriesId") Long seriesId,
            @Param("userId") Long userId
    );

    /**
     * 최신순 목록 (post_id DESC 커서).
     */
    List<PostListItemDto> selectLatestPosts(
            @Param("seriesId") Long seriesId,
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );

    /**
     * 인기순 목록 (기간 내 좋아요 수 DESC, post_id DESC 커서).
     */
    List<PostListItemDto> selectPopularPosts(
            @Param("seriesId") Long seriesId,
            @Param("userId") Long userId,
            @Param("periodStart") LocalDateTime periodStart,
            @Param("lastValue") Long lastValue,
            @Param("lastId") Long lastId,
            @Param("size") int size
    );

    /**
     * 게시글 상세(이미지 목록 제외). 삭제된 글은 조회되지 않음.
     */
    PostDetailRawDto selectPostDetail(
            @Param("postId") Long postId,
            @Param("userId") Long userId
    );

    List<Long> selectPostImageIds(@Param("postId") Long postId);

    /**
     * 소유자/존재 확인용. 삭제 여부와 관계없이 조회(삭제 글도 NOT_FOUND 처리 위해 서비스에서 판단).
     */
    CommunityPost selectPostForCheck(@Param("postId") Long postId);

    void insertPost(CommunityPost post);

    void insertPostImages(
            @Param("postId") Long postId,
            @Param("imageIds") List<Long> imageIds
    );

    void deletePostImages(@Param("postId") Long postId);

    void updatePost(
            @Param("postId") Long postId,
            @Param("userId") Long userId,
            @Param("request") PostUpdateRequest request
    );

    void softDeletePost(
            @Param("postId") Long postId,
            @Param("userId") Long userId
    );

    void increaseViewCount(@Param("postId") Long postId);

    void increaseLikeCount(@Param("postId") Long postId);

    void decreaseLikeCount(@Param("postId") Long postId);
}
