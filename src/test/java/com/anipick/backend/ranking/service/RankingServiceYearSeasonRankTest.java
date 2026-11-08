package com.anipick.backend.ranking.service;

import com.anipick.backend.anime.mapper.GenreMapper;
import com.anipick.backend.ranking.mapper.RankingMapper;
import com.anipick.backend.ranking.mapper.RealTimeRankingMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RankingServiceYearSeasonRankTest {
    @Mock
    RankingMapper rankingMapper;
    @Mock
    RealTimeRankingMapper realTimeRankingMapper;
    @Mock
    GenreMapper genreMapper;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    RedisTemplate<String, String> redisTemplate;

    RankingService sut;

    @BeforeEach
    void setUp() {
        sut = new RankingService(rankingMapper, realTimeRankingMapper, genreMapper, objectMapper, redisTemplate);
    }

    @Test
    @DisplayName("연도·분기의 날짜 범위로 순위를 조회한다")
    void rankByYearSeasonRange() {
        // given
        given(rankingMapper.getYearSeasonRankByAnimeId(1L, "2025-07-01", "2025-09-30")).willReturn(3L);

        // when
        Long rank = sut.getYearSeasonRank(1L, 2025, 3);

        // then
        assertThat(rank).isEqualTo(3L);
    }

    @Test
    @DisplayName("4분기는 12월 31일까지 범위로 조회한다")
    void rankByFourthQuarterRange() {
        // given
        given(rankingMapper.getYearSeasonRankByAnimeId(1L, "2020-10-01", "2020-12-31")).willReturn(2L);

        // when
        Long rank = sut.getYearSeasonRank(1L, 2020, 4);

        // then
        assertThat(rank).isEqualTo(2L);
    }

    @Test
    @DisplayName("랭킹 제외 대상이면(쿼리 결과 null) null을 반환한다")
    void excludedReturnsNull() {
        // given
        given(rankingMapper.getYearSeasonRankByAnimeId(1L, "2025-01-01", "2025-03-31")).willReturn(null);

        // when
        Long rank = sut.getYearSeasonRank(1L, 2025, 1);

        // then
        assertThat(rank).isNull();
    }
}
