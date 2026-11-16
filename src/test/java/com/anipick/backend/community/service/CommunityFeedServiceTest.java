package com.anipick.backend.community.service;

import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.community.dto.FeedPageDto;
import com.anipick.backend.community.dto.FeedPostRawDto;
import com.anipick.backend.community.mapper.CommunityFeedMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class CommunityFeedServiceTest {

    @Mock
    CommunityFeedMapper communityFeedMapper;

    CommunityFeedService sut;

    @BeforeEach
    void setUp() {
        sut = new CommunityFeedService(communityFeedMapper);
    }

    private static FeedPostRawDto post(long postId, Long periodLikeCount) {
        return new FeedPostRawDto(postId, 1L, null, "시리즈", null, null, null,
                2L, "닉네임", null, "제목", "본문", null, 0L, false, 0L, 0L, 0L,
                "2026-11-12 10:20:30", periodLikeCount);
    }

    @Test
    @DisplayName("전체 피드는 마지막 post_id 를 커서로 주고, 시리즈 제목과 작성일을 가공한다")
    void allFeedCursorAndFormat() {
        given(communityFeedMapper.selectAllFeedPosts(3L, null, 20))
                .willReturn(List.of(post(9, null), post(7, null)));

        FeedPageDto result = sut.getAllFeed(null, 20, 3L);

        assertThat(result.getCursor().getLastId()).isEqualTo(7L);
        assertThat(result.getCursor().getLastValue()).isNull();
        assertThat(result.getPosts().get(0).getSeriesTitle()).isEqualTo("시리즈");
        assertThat(result.getPosts().get(0).getCreatedAt()).isEqualTo("2026. 11. 12");
    }

    @Test
    @DisplayName("시청중 피드는 WATCHING 상태로 조회하고, 결과가 없으면 lastId null 커서를 준다")
    void watchingFeedEmpty() {
        given(communityFeedMapper.selectWatchingFeedPosts(3L, "WATCHING", null, 20)).willReturn(List.of());

        FeedPageDto result = sut.getWatchingFeed(null, 20, 3L);

        assertThat(result.getPosts()).isEmpty();
        assertThat(result.getCursor().getLastId()).isNull();
    }

    @Test
    @DisplayName("인기 피드 커서는 (7일 좋아요 수, post_id)")
    void popularFeedCursor() {
        given(communityFeedMapper.selectPopularFeedPosts(eq(3L), any(), eq(5L), eq(10L), eq(20)))
                .willReturn(List.of(post(8, 5L), post(4, 3L)));

        FeedPageDto result = sut.getPopularFeed("5", 10L, 20, 3L);

        assertThat(result.getCursor().getLastId()).isEqualTo(4L);
        assertThat(result.getCursor().getLastValue()).isEqualTo("3");
        assertThat(result.getCursor().getSort()).isNull();
    }

    @Test
    @DisplayName("인기 피드 결과가 없으면 lastId·lastValue 가 모두 null 인 커서를 준다")
    void popularFeedEmpty() {
        given(communityFeedMapper.selectPopularFeedPosts(eq(3L), any(), eq(null), eq(null), eq(20)))
                .willReturn(List.of());

        FeedPageDto result = sut.getPopularFeed(null, null, 20, 3L);

        assertThat(result.getPosts()).isEmpty();
        assertThat(result.getCursor().getLastId()).isNull();
        assertThat(result.getCursor().getLastValue()).isNull();
    }

    @Test
    @DisplayName("인기 커서는 lastValue 와 lastId 중 하나만 오거나 숫자가 아니면 BAD_REQUEST")
    void popularCursorValidation() {
        assertThat(CommunityFeedService.parsePopularCursorValue(null, null)).isNull();
        assertThat(CommunityFeedService.parsePopularCursorValue(" 3 ", 1L)).isEqualTo(3L);

        assertThatThrownBy(() -> CommunityFeedService.parsePopularCursorValue("3", null))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.BAD_REQUEST);
        assertThatThrownBy(() -> CommunityFeedService.parsePopularCursorValue(null, 1L))
                .isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> CommunityFeedService.parsePopularCursorValue("abc", 1L))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("size 가 0 이하이면 매퍼를 호출하지 않고 BAD_REQUEST")
    void sizeValidation() {
        assertThatThrownBy(() -> sut.getAllFeed(null, -1, 3L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.BAD_REQUEST);
        assertThatThrownBy(() -> sut.getWatchingFeed(null, 0, 3L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.BAD_REQUEST);
        assertThatThrownBy(() -> sut.getPopularFeed(null, null, -5, 3L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.BAD_REQUEST);

        verifyNoInteractions(communityFeedMapper);
    }
}
