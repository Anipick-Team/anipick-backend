package com.anipick.backend.anime.service;

import com.anipick.backend.anime.domain.AnimeSocialMetric;
import com.anipick.backend.anime.domain.AnimeSocialPhase;
import com.anipick.backend.anime.domain.AnimeStatus;
import com.anipick.backend.anime.dto.AnimeDetailInfoItemDto;
import com.anipick.backend.anime.dto.AnimeDetailInfoResultDto;
import com.anipick.backend.anime.dto.AnimeSocialCountDto;
import com.anipick.backend.anime.mapper.AnimeMapper;
import com.anipick.backend.anime.mapper.GenreMapper;
import com.anipick.backend.anime.mapper.StudioMapper;
import com.anipick.backend.ranking.service.RankingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * getAnimeInfoDetail 의 소셜 지표 필드 매핑 검증
 * (오늘 날짜를 주입할 수 없으므로 날짜와 무관한 NOT_YET_RELEASED / FINISHED / CANCELLED 로 검증)
 */
@ExtendWith(MockitoExtension.class)
class AnimeServiceSocialIndicatorTest {
    private static final Long ANIME_ID = 1L;
    private static final Long USER_ID = 2L;

    @Mock
    AnimeMapper mapper;
    @Mock
    AnimeCacheService animeCacheService;
    @Mock
    GenreMapper genreMapper;
    @Mock
    StudioMapper studioMapper;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    RedisTemplate<String, Object> redisTemplate;
    // #439 병합 후 AnimeService 생성자에 주입됨 (startDate 가 있으면 순위 조회에서 호출)
    @Mock
    RankingService rankingService;

    @InjectMocks
    AnimeService sut;

    private void givenDetail(AnimeStatus status, long watchlist, long watching, long finished) {
        AnimeDetailInfoItemDto detail = mock(AnimeDetailInfoItemDto.class);
        given(detail.getStatus()).willReturn(status);
        given(mapper.selectAnimeInfoDetail(ANIME_ID, USER_ID)).willReturn(detail);

        AnimeSocialCountDto count = mock(AnimeSocialCountDto.class);
        given(count.getWatchlistCount()).willReturn(watchlist);
        given(count.getWatchingCount()).willReturn(watching);
        given(count.getFinishedCount()).willReturn(finished);
        given(mapper.selectAnimeSocialCount(ANIME_ID)).willReturn(count);
    }

    @Test
    @DisplayName("방영 예정 애니 상세는 볼 애니 수를 (NOT_YET_RELEASED, WANT_TO_WATCH, count) 로 응답한다.")
    void notYetReleasedUsesWatchlistCount() {
        // given - 세 수를 모두 다르게 두어 인자 순서가 뒤바뀌면 실패하도록 한다
        givenDetail(AnimeStatus.NOT_YET_RELEASED, 12L, 50L, 60L);
        // when
        AnimeDetailInfoResultDto result = sut.getAnimeInfoDetail(ANIME_ID, USER_ID);
        // then
        assertEquals(AnimeStatus.NOT_YET_RELEASED, result.getAiringStatus());
        assertEquals(AnimeSocialPhase.NOT_YET_RELEASED, result.getSocialPhase());
        assertEquals(AnimeSocialMetric.WANT_TO_WATCH, result.getMetric());
        assertEquals(12L, result.getCount());
    }

    @Test
    @DisplayName("방영 종료 애니 상세에서 다 본 애니가 더 많으면 (FINISHED, COMPLETED, 다 본 애니 수) 로 응답한다.")
    void finishedUsesFinishedCount() {
        // given
        givenDetail(AnimeStatus.FINISHED, 100L, 7L, 30L);
        // when
        AnimeDetailInfoResultDto result = sut.getAnimeInfoDetail(ANIME_ID, USER_ID);
        // then
        assertEquals(AnimeStatus.FINISHED, result.getAiringStatus());
        assertEquals(AnimeSocialPhase.FINISHED, result.getSocialPhase());
        assertEquals(AnimeSocialMetric.COMPLETED, result.getMetric());
        assertEquals(30L, result.getCount());
    }

    @Test
    @DisplayName("방영 종료 애니 상세에서 보는 중이 더 많으면 (FINISHED, WATCHING, 보는 중 수) 로 응답한다.")
    void finishedUsesWatchingCount() {
        // given
        givenDetail(AnimeStatus.FINISHED, 100L, 30L, 7L);
        // when
        AnimeDetailInfoResultDto result = sut.getAnimeInfoDetail(ANIME_ID, USER_ID);
        // then
        assertEquals(AnimeSocialPhase.FINISHED, result.getSocialPhase());
        assertEquals(AnimeSocialMetric.WATCHING, result.getMetric());
        assertEquals(30L, result.getCount());
    }

    @Test
    @DisplayName("방영 취소 애니 상세는 socialPhase, metric, count 가 모두 null 이다.")
    void cancelledIsHidden() {
        // given
        givenDetail(AnimeStatus.CANCELLED, 100L, 100L, 100L);
        // when
        AnimeDetailInfoResultDto result = sut.getAnimeInfoDetail(ANIME_ID, USER_ID);
        // then
        assertEquals(AnimeStatus.CANCELLED, result.getAiringStatus());
        assertNull(result.getSocialPhase());
        assertNull(result.getMetric());
        assertNull(result.getCount());
    }

    @Test
    @DisplayName("소셜 집계 결과가 null 이면 상세 조회는 유지하고 socialPhase, metric, count 만 null 로 응답한다.")
    void nullSocialCountIsHidden() {
        // given - 소셜 지표가 노출되는 상태(NOT_YET_RELEASED)로 두어 null 처리 분기만으로 숨겨지는지 확인한다
        AnimeDetailInfoItemDto detail = mock(AnimeDetailInfoItemDto.class);
        given(detail.getStatus()).willReturn(AnimeStatus.NOT_YET_RELEASED);
        given(mapper.selectAnimeInfoDetail(ANIME_ID, USER_ID)).willReturn(detail);
        given(mapper.selectAnimeSocialCount(ANIME_ID)).willReturn(null);
        // when
        AnimeDetailInfoResultDto result = sut.getAnimeInfoDetail(ANIME_ID, USER_ID);
        // then
        assertEquals(AnimeStatus.NOT_YET_RELEASED, result.getAiringStatus());
        assertNull(result.getSocialPhase());
        assertNull(result.getMetric());
        assertNull(result.getCount());
    }
}
