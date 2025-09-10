package com.anipick.backend.ranking.service;

import com.anipick.backend.anime.dto.GenreDto;
import com.anipick.backend.anime.mapper.GenreMapper;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.ranking.dto.*;
import com.anipick.backend.ranking.mapper.RankingMapper;
import com.anipick.backend.ranking.mapper.RealTimeRankingMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {
//    @Mock
//    RankingMapper rankingMapper;
//
//    @Mock
//    RealTimeRankingMapper realTimeRankingMapper;
//
//    @Mock
//    GenreMapper genreMapper;
//    @Mock
//    ObjectMapper objectMapper;
//
//    @Mock
//    RedisTemplate<String, String> redisTemplate;
//
//    @Mock
//    ValueOperations<String, String> valueOps;
//
//    RankingService sut; // System Under Test
//
//    @BeforeEach
//    void setUp() {
//        sut = new RankingService(rankingMapper, realTimeRankingMapper, genreMapper, objectMapper, redisTemplate);
//    }
//
//    // region -------- getRealTimeRanking --------
//
//    @Test
//    void getRealTimeRanking_정상_변동계산_AND_커서세팅() throws Exception {
//        // given
//        when(redisTemplate.opsForValue()).thenReturn(valueOps);
//        String genre = null;
//        Long lastId = 0L;
//        Long lastValue = 0L;
//        int size = 2;
//
//        // Redis JSON → objectMapper.readValue(...) 로 반환될 리스트 (animeId 기준: [2,1,3])
//        RedisRealTimeRankingAnimesDto r1 = mock(RedisRealTimeRankingAnimesDto.class);
//        RedisRealTimeRankingAnimesDto r2 = mock(RedisRealTimeRankingAnimesDto.class);
//        RedisRealTimeRankingAnimesDto r3 = mock(RedisRealTimeRankingAnimesDto.class);
//        when(r1.getAnimeId()).thenReturn(2L);
//        when(r2.getAnimeId()).thenReturn(1L);
//        when(r3.getAnimeId()).thenReturn(3L);
//        when(valueOps.get(anyString())).thenReturn("[dummy-json]");
//        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
//                .thenReturn(List.of(r1, r2, r3));
//
//        // DB 전체 랭킹 (id: [1,2,3] → dbRank 1,2,3)
//        RealTimeRankingAnimesFromQueryDto d1 = mockDbRow(1L);
//        RealTimeRankingAnimesFromQueryDto d2 = mockDbRow(2L);
//        RealTimeRankingAnimesFromQueryDto d3 = mockDbRow(3L);
//        when(realTimeRankingMapper.getRealTimeRanking()).thenReturn(List.of(d1, d2, d3));
//
//        // 페이징 결과(상위 2개만)
//        when(realTimeRankingMapper.getRealTimeRankingPaging(eq(lastValue), eq(lastId), eq(size)))
//                .thenReturn(List.of(d1, d2));
//
//        // 장르 매핑 (첫 페이지의 animeIds = [2,1])
//        when(rankingMapper.getGenresByAnimeIds(eq(List.of(2L, 1L))))
//                .thenReturn(List.of(
//                        genre(2L, 1L, "액션"),
//                        genre(1L, 6L, "코미디")
//                ));
//
//        // RealTimeRankingAnimesDto.from(...) 정적 모킹
//        RealTimeRankingAnimesDto a1 = mock(RealTimeRankingAnimesDto.class);
//        RealTimeRankingAnimesDto a2 = mock(RealTimeRankingAnimesDto.class);
//        // 마지막 아이템의 pop/trend 커서 읽힘
//        when(a2.getPopularity()).thenReturn(222L);
//        when(a2.getTrending()).thenReturn(888L);
//
//        try (MockedStatic<RealTimeRankingAnimesDto> mocked = Mockito.mockStatic(RealTimeRankingAnimesDto.class)) {
//            // diff: d1 → redisRank(2) - dbRank(1) = 1 → trend "up"
//            // diff: d2 → redisRank(1) - dbRank(2) = -1 → trend "down"
//            mocked.when(() -> RealTimeRankingAnimesDto.from(eq(1L), eq("1"), eq("up"), same(d1), anyList()))
//                    .thenReturn(a1);
//            mocked.when(() -> RealTimeRankingAnimesDto.from(eq(2L), eq("-1"), eq("down"), same(d2), anyList()))
//                    .thenReturn(a2);
//
//            // when
//            RealTimeRankingResponse res = sut.getRealTimeRanking(genre, lastId, lastValue, size);
//
//            // then
//            assertThat(res).isNotNull();
//            assertThat(res.getAnimes()).hasSize(2);
//            // 커서: 마지막 아이템의 pop/trend 사용
//            assertThat(res.getCursor().getLastId()).isEqualTo(222L);
//            assertThat(res.getCursor().getLastValue()).isEqualTo("888");
//
//            // 정적 from 호출 검증
//            mocked.verify(() -> RealTimeRankingAnimesDto.from(eq(1L), eq("1"), eq("up"), same(d1), anyList()));
//            mocked.verify(() -> RealTimeRankingAnimesDto.from(eq(2L), eq("-1"), eq("down"), same(d2), anyList()));
//        }
//    }
//
//    @Test
//    void getRealTimeRanking_신규진입_N_분기_동작() throws Exception {
//        // given
//        when(redisTemplate.opsForValue()).thenReturn(valueOps);
//        Long lastId = 0L;
//        Long lastValue = 0L;
//        int size = 1;
//
//        // Redis(빈 리스트처럼 만들어 특정 id가 없도록)
//        when(valueOps.get(anyString())).thenReturn("[dummy-json]");
//        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
//                .thenReturn(List.of()); // redis에 아무 것도 없음 → redisRank == null
//
//        // DB 전체 + 페이징(1개)
//        RealTimeRankingAnimesFromQueryDto d1 = mockDbRow(10L);
//        when(realTimeRankingMapper.getRealTimeRanking()).thenReturn(List.of(d1));
//        when(realTimeRankingMapper.getRealTimeRankingPaging(eq(lastValue), eq(lastId), eq(size)))
//                .thenReturn(List.of(d1));
//
//        // 장르
//        when(rankingMapper.getGenresByAnimeIds(eq(List.of( /* sliced: redis 기준이 비어도 DB 페이징 animeIds는 따로*/ ))))
//                .thenReturn(List.of()); // 안전하게 빈 리스트
//
//        // from 모킹: redisRank == null && dbRank != null → change="N", trend="new"
//        RealTimeRankingAnimesDto a1 = mock(RealTimeRankingAnimesDto.class);
//        when(a1.getPopularity()).thenReturn(500L);
//        when(a1.getTrending()).thenReturn(600L);
//
//        try (MockedStatic<RealTimeRankingAnimesDto> mocked = Mockito.mockStatic(RealTimeRankingAnimesDto.class)) {
//            mocked.when(() -> RealTimeRankingAnimesDto.from(eq(1L), eq("N"), eq("new"), same(d1), anyList()))
//                    .thenReturn(a1);
//
//            // when
//            RealTimeRankingResponse res = sut.getRealTimeRanking(null, lastId, lastValue, size);
//
//            // then
//            assertThat(res.getAnimes()).hasSize(1);
//            assertThat(res.getCursor().getLastId()).isEqualTo(500L);
//            assertThat(res.getCursor().getLastValue()).isEqualTo("600");
//
//            mocked.verify(() -> RealTimeRankingAnimesDto.from(eq(1L), eq("N"), eq("new"), same(d1), anyList()));
//        }
//    }
//
//    @Test
//    void getRealTimeRanking_페이지없음이면_cursor_null_처리() throws Exception {
//        // given
//        when(redisTemplate.opsForValue()).thenReturn(valueOps);
//        when(valueOps.get(anyString())).thenReturn("[dummy-json]");
//        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
//                .thenReturn(List.of()); // redis 리스트 비어도 무방
//        when(realTimeRankingMapper.getRealTimeRanking()).thenReturn(List.of());
//        when(realTimeRankingMapper.getRealTimeRankingPaging(any(), any(), anyInt()))
//                .thenReturn(List.of()); // 페이징 결과 없음
//
//        // when
//        RealTimeRankingResponse res = sut.getRealTimeRanking(null, null, null, 20);
//
//        // then
//        assertThat(res.getAnimes()).isEmpty();
//        // 구현 상 "null" 문자열을 넣고 있음
//        assertThat(res.getCursor().getLastId()).isNull();
//        assertThat(res.getCursor().getLastValue()).isEqualTo("null");
//    }
//
//    // endregion
//
//    // region -------- getYearSeasonRanking --------
//
//    @Test
//    void getYearSeasonRanking_displayRank_증가_AND_커서세팅() throws Exception {
//        // given
//        Integer year = 2024;
//        Integer season = 1;
//        String genre = "액션";
//        Long lastId = 0L;
//        Long lastRank = 20L;
//        int size = 2;
//
//        when(genreMapper.findGenreIdByGenreName(eq(genre))).thenReturn(1L);
//
//        RankingAnimesFromQueryDto q1 = rowIdOnly(100L);
//        RankingAnimesFromQueryDto q2 = rowWithPopularity(200L, 800L);
//        when(rankingMapper.getYearSeasonRankingPaging(eq(year), eq(season), eq(1L), eq(lastId), eq(size)))
//                .thenReturn(List.of(q1, q2));
//
//        when(rankingMapper.getGenresByAnimeIds(eq(List.of(100L, 200L))))
//                .thenReturn(List.of(
//                        genre(100L, 1L,"액션"),
//                        genre(200L, 6L,"코미디")
//                ));
//
//        try (MockedStatic<RankingAnimesDto> mocked = Mockito.mockStatic(RankingAnimesDto.class)) {
//            // displayRank: lastRank(20) → 21, 22 순서로 들어가야 함
//            RankingAnimesDto a1 = mock(RankingAnimesDto.class);
//            RankingAnimesDto a2 = mock(RankingAnimesDto.class);
//
//            mocked.when(() -> RankingAnimesDto.from(eq(21L), same(q1), anyList())).thenReturn(a1);
//            mocked.when(() -> RankingAnimesDto.from(eq(22L), same(q2), anyList())).thenReturn(a2);
//
//            // when
//            RankingResponse res = sut.getYearSeasonRanking(year, season, genre, lastId, lastRank, size);
//
//            // then
//            assertThat(res.getAnimes()).hasSize(2);
//            // 커서: 마지막 아이템의 popularity 사용
//            assertThat(res.getCursor().getLastId()).isEqualTo(800L);
//
//            mocked.verify(() -> RankingAnimesDto.from(eq(21L), same(q1), anyList()));
//            mocked.verify(() -> RankingAnimesDto.from(eq(22L), same(q2), anyList()));
//        }
//    }
//
//    @Test
//    void getYearSeasonRanking_페이지없음이면_cursor_null() throws Exception {
//        when(rankingMapper.getYearSeasonRankingPaging(any(), any(), any(), any(), any()))
//                .thenReturn(List.of());
//        RankingResponse res = sut.getYearSeasonRanking(2024, 1, null, null, 0L, 20);
//        assertThat(res.getAnimes()).isEmpty();
//        assertThat(res.getCursor().getLastId()).isNull();
//    }
//
//    // endregion
//
//    // region -------- getAllTimeRanking --------
//
//    @Test
//    void getAllTimeRanking_displayRank_증가_AND_커서세팅() {
//        // given
//        String genre = null;
//        Long lastId = 0L;
//        Long lastRank = 40L;
//        int size = 2;
//
//        RankingAnimesFromQueryDto q1 = rowIdOnly(1000L);
//        RankingAnimesFromQueryDto q2 = rowWithPopularity(2000L, 950L);
//        when(rankingMapper.getAllTimeRankingPaging(isNull(), eq(lastId), eq(size)))
//                .thenReturn(List.of(q1, q2));
//
//        when(rankingMapper.getGenresByAnimeIds(eq(List.of(1000L, 2000L))))
//                .thenReturn(List.of());
//
//        try (MockedStatic<RankingAnimesDto> mocked = Mockito.mockStatic(RankingAnimesDto.class)) {
//            RankingAnimesDto a1 = mock(RankingAnimesDto.class);
//            RankingAnimesDto a2 = mock(RankingAnimesDto.class);
//
//            mocked.when(() -> RankingAnimesDto.from(eq(41L), same(q1), anyList())).thenReturn(a1);
//            mocked.when(() -> RankingAnimesDto.from(eq(42L), same(q2), anyList())).thenReturn(a2);
//
//            // when
//            RankingResponse res = sut.getAllTimeRanking(genre, lastId, lastRank, size);
//
//            // then
//            assertThat(res.getAnimes()).hasSize(2);
//            assertThat(res.getCursor().getLastId()).isEqualTo(950L);
//
//            mocked.verify(() -> RankingAnimesDto.from(eq(41L), same(q1), anyList()));
//            mocked.verify(() -> RankingAnimesDto.from(eq(42L), same(q2), anyList()));
//        }
//    }
//
//    @Test
//    void getAllTimeRanking_페이지없음이면_cursor_null() {
//        when(rankingMapper.getAllTimeRankingPaging(any(), any(), anyInt()))
//                .thenReturn(List.of());
//        RankingResponse res = sut.getAllTimeRanking(null, null, 0L, 20);
//        assertThat(res.getAnimes()).isEmpty();
//        assertThat(res.getCursor().getLastId()).isNull();
//    }
//
//    // endregion
//
//    // region -------- helpers (mocks) --------
//
//    private RealTimeRankingAnimesFromQueryDto mockDbRow(Long id) {
//        RealTimeRankingAnimesFromQueryDto dto = mock(RealTimeRankingAnimesFromQueryDto.class);
//        when(dto.getAnimeId()).thenReturn(id);
//        return dto;
//    }
//
//    private RankingAnimesFromQueryDto rowIdOnly(Long id) {
//        RankingAnimesFromQueryDto dto = mock(RankingAnimesFromQueryDto.class);
//        when(dto.getAnimeId()).thenReturn(id);
//        return dto;
//    }
//
//    private RankingAnimesFromQueryDto rowWithPopularity(Long id, Long popularity) {
//        RankingAnimesFromQueryDto dto = mock(RankingAnimesFromQueryDto.class);
//        when(dto.getAnimeId()).thenReturn(id);
//        when(dto.getPopularity()).thenReturn(popularity);
//        return dto;
//    }
//
//    private AnimeGenresDto genre(Long animeId, Long genreId, String name) {
//        return new AnimeGenresDto(animeId, genreId, name);
//    }
}