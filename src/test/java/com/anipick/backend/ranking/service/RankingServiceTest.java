package com.anipick.backend.ranking.service;

import com.anipick.backend.anime.mapper.GenreMapper;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.ranking.domain.RankingDefaults;
import com.anipick.backend.ranking.dto.*;
import com.anipick.backend.ranking.mapper.RankingMapper;
import com.anipick.backend.ranking.mapper.RealTimeRankingMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {
    @Mock RankingMapper rankingMapper;
    @Mock RealTimeRankingMapper realTimeRankingMapper;
    @Mock GenreMapper genreMapper;
    @Mock RedisTemplate<String, String> redisTemplate;
    @Mock ValueOperations<String, String> valueOps;

    ObjectMapper objectMapper; // real
    RankingService sut;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        sut = new RankingService(rankingMapper, realTimeRankingMapper, genreMapper, objectMapper, redisTemplate);
    }

    // =========================
    // getRealTimeRanking tests
    // =========================

    @Test
    @DisplayName("실시간 랭킹: 장르지정 + 정상 플로우 (diff 계산, trend, cursor 계산)")
    void getRealTimeRanking_withGenre_success() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        String genre = "액션";
        Long genreId = 1L;
        when(genreMapper.findGenreIdByGenreName(genre)).thenReturn(genreId);

        // Redis 키
        String expectedKey = RankingDefaults.RANKING_ALIAS_KEY + genreId + RankingDefaults.COLON + RankingDefaults.CURRENT;

        // Redis JSON: real_time_rank 순서가 Redis상 랭크(1-base)
        // ex) [20, 10, 30] 이면 20이 1위, 10이 2위, 30이 3위
        String redisJson = """
        { "real_time_rank": [ 20, 10, 30 ] }
        """;
        when(valueOps.get(expectedKey)).thenReturn(redisJson);

        // DB 전체 순위(랭크 계산용): 10,20,30 → DB rank: 10->1, 20->2, 30->3
        RealTimeRankingAnimesFromQueryDto all1 = rtRow(10L, "T10", "U10",  900L, 1000L);
        RealTimeRankingAnimesFromQueryDto all2 = rtRow(20L, "T20", "U20",  800L,  900L);
        RealTimeRankingAnimesFromQueryDto all3 = rtRow(30L, "T30", "U30",  700L,  800L);
        when(realTimeRankingMapper.getRealTimeRanking())
                .thenReturn(List.of(all1, all2, all3));

        // 페이징 결과(커서 계산에 사용): 10, 30
        RealTimeRankingAnimesFromQueryDto p1 = rtRow(10L, "T10", "U10", 800L, 900L);
        RealTimeRankingAnimesFromQueryDto p2 = rtRow(30L, "T30", "U30", 700L, 800L);
        when(realTimeRankingMapper.getRealTimeRankingPaging(eq(0L), eq(0L), eq(2)))
                .thenReturn(List.of(p1, p2));

        // 장르 매핑
        when(rankingMapper.getGenresByAnimeIds(List.of(10L, 30L)))
                .thenReturn(List.of(
                        genreOf(10L, 1L, "액션"),
                        genreOf(30L, 2L, "코미디"),
                        genreOf(30L, 6L, "판타지")
                ));

        // when
        RealTimeRankingResponse resp = sut.getRealTimeRanking(genre, 0L, 0L, 2);

        // then
        // Redis get key 검증
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOps).get(keyCaptor.capture());
        assertThat(keyCaptor.getValue()).isEqualTo(expectedKey);

        // 응답 검증
        assertThat(resp).isNotNull();
        assertThat(resp.getAnimes()).hasSize(2);

        // Redis rank: {20:1, 10:2, 30:3}
        // DB   rank: {10:1, 20:2, 30:3}
        // p1=10 → diff = 2-1 = 1 → trend up
        // p2=30 → diff = 3-3 = 0 → trend same
        RealTimeRankingAnimesDto a1 = resp.getAnimes().get(0);
        RealTimeRankingAnimesDto a2 = resp.getAnimes().get(1);

        assertThat(a1.getAnimeId()).isEqualTo(10L);
        assertThat(a1.getRank()).isEqualTo(1L); // DB rank
        assertThat(a1.getChange()).isEqualTo("1");
        assertThat(a1.getTrend()).isEqualTo("up");
        assertThat(a1.getGenres()).containsExactly("액션"); // specificGenre 우선

        assertThat(a2.getAnimeId()).isEqualTo(30L);
        assertThat(a2.getRank()).isEqualTo(3L);
        assertThat(a2.getChange()).isEqualTo("0");
        assertThat(a2.getTrend()).isEqualTo("same");
        assertThat(a2.getGenres()).containsExactlyInAnyOrder("코미디", "판타지");

        // 커서: 마지막 요소의 popularity, trending
        assertThat(resp.getCursor()).isNotNull();
        assertThat(resp.getCursor().getSort()).isEqualTo(RankingDefaults.SORT);
        assertThat(resp.getCursor().getLastId()).isEqualTo(p2.getPopularity());
        assertThat(resp.getCursor().getLastValue()).isEqualTo(String.valueOf(p2.getTrending()));
    }

    @Test
    @DisplayName("실시간 랭킹: 장르 미지정 + 결과 비어있음 → cursor(sort, null, \"null\") & 빈 리스트")
    void getRealTimeRanking_noGenre_emptyResult() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        String expectedKey = RankingDefaults.RANKING_GENRE_ALL_KEY + RankingDefaults.COLON + RankingDefaults.CURRENT;
        String redisJson = """
        { "real_time_rank": [ ] }
        """;
        when(valueOps.get(expectedKey)).thenReturn(redisJson);

        when(realTimeRankingMapper.getRealTimeRanking()).thenReturn(List.of());
        when(realTimeRankingMapper.getRealTimeRankingPaging(any(), any(), any())).thenReturn(List.of());
        when(rankingMapper.getGenresByAnimeIds(anyList())).thenReturn(List.of());

        // when
        RealTimeRankingResponse resp = sut.getRealTimeRanking(null, 0L, 0L, 20);

        // then
        verify(valueOps).get(expectedKey);

        assertThat(resp).isNotNull();
        assertThat(resp.getAnimes()).isEmpty();
        assertThat(resp.getCursor()).isNotNull();
        assertThat(resp.getCursor().getSort()).isEqualTo(RankingDefaults.SORT);
        assertThat(resp.getCursor().getLastId()).isNull();
        assertThat(resp.getCursor().getLastValue()).isEqualTo("null");
    }

    @Test
    @DisplayName("실시간 랭킹: Redis JSON 불량 → JsonProcessingException 발생 → CustomException(INTERNAL_SERVER_ERROR)")
    void getRealTimeRanking_badJson_throwsCustom() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        String key = RankingDefaults.RANKING_GENRE_ALL_KEY + RankingDefaults.COLON + RankingDefaults.CURRENT;
        String badJson = "{ not-a-valid-json ";
        when(valueOps.get(key)).thenReturn(badJson);

        // when / then
        assertThatThrownBy(() -> sut.getRealTimeRanking(null, 0L, 0L, 10))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INTERNAL_SERVER_ERROR.getErrorReason());
    }

    // =========================
    // getYearSeasonRanking tests
    // =========================

    @Test
    @DisplayName("시즌 랭킹: 장르 지정 + 표시랭크 증가 + 커서(popularity)")
    void getYearSeasonRanking_success() throws Exception {
        // given
        String genre = "액션";
        when(genreMapper.findGenreIdByGenreName(genre)).thenReturn(1L);

        RankingAnimesFromQueryDto r1 = seasonRow(100L, "A100", "C100", 700L);
        RankingAnimesFromQueryDto r2 = seasonRow(200L, "A200", "C200", 800L);
        when(rankingMapper.getYearSeasonRankingPaging(2024, 1, 1L, 0L, 2))
                .thenReturn(List.of(r1, r2));

        when(rankingMapper.getGenresByAnimeIds(List.of(100L, 200L)))
                .thenReturn(List.of(
                        genreOf(100L, 1L, "액션"),
                        genreOf(200L, 2L, "코미디")
                ));

        // when
        RankingResponse resp = sut.getYearSeasonRanking(2024, 1, genre, 0L, 0L, 2);

        // then
        assertThat(resp).isNotNull();
        assertThat(resp.getAnimes()).hasSize(2);

        RankingAnimesDto a1 = resp.getAnimes().get(0);
        RankingAnimesDto a2 = resp.getAnimes().get(1);

        // 표시 랭크: lastRank=0에서 시작 → 1, 2
        assertThat(a1.getAnimeId()).isEqualTo(100L);
        assertThat(a1.getRank()).isEqualTo(1L);
        assertThat(a1.getGenres()).containsExactly("액션");

        assertThat(a2.getAnimeId()).isEqualTo(200L);
        assertThat(a2.getRank()).isEqualTo(2L);
        assertThat(a2.getGenres()).containsExactly("코미디");

        // 커서: 마지막 요소의 popularity
        assertThat(resp.getCursor()).isNotNull();
        assertThat(resp.getCursor().getLastId()).isEqualTo(800L);
    }

    @Test
    @DisplayName("시즌 랭킹: 결과 없음 → cursor(null) + 빈 리스트")
    void getYearSeasonRanking_empty() throws Exception {
        when(rankingMapper.getYearSeasonRankingPaging(any(), any(), any(), any(), any()))
                .thenReturn(List.of());
        when(rankingMapper.getGenresByAnimeIds(anyList())).thenReturn(List.of());

        RankingResponse resp = sut.getYearSeasonRanking(2024, 1, null, 0L, 0L, 20);

        assertThat(resp.getAnimes()).isEmpty();
        assertThat(resp.getCursor()).isNotNull();
        assertThat(resp.getCursor().getLastId()).isNull();
    }

    // =========================
    // getAllTimeRanking tests
    // =========================

    @Test
    @DisplayName("올타임 랭킹: 표시랭크 증가 + 커서(popularity)")
    void getAllTimeRanking_success() {
        // given
        RankingAnimesFromQueryDto a1 = seasonRow(100L, "A100", "C100", 500L);
        RankingAnimesFromQueryDto a2 = seasonRow(200L, "A200", "C200", 600L);
        when(rankingMapper.getAllTimeRankingPaging(null, 0L, 2))
                .thenReturn(List.of(a1, a2));

        when(rankingMapper.getGenresByAnimeIds(List.of(100L, 200L)))
                .thenReturn(List.of(genreOf(100L, 9L, "판타지")));

        // lastRank=10에서 시작 → 11, 12
        RankingResponse resp = sut.getAllTimeRanking(null, 0L, 10L, 2);

        assertThat(resp.getAnimes()).hasSize(2);
        RankingAnimesDto r1 = resp.getAnimes().get(0);
        RankingAnimesDto r2 = resp.getAnimes().get(1);

        assertThat(r1.getRank()).isEqualTo(11L);
        assertThat(r2.getRank()).isEqualTo(12L);

        assertThat(resp.getCursor()).isNotNull();
        assertThat(resp.getCursor().getLastId()).isEqualTo(600L);
    }

    // =========================
    // Helpers (실객체용 빌더)
    // =========================

    private RealTimeRankingAnimesFromQueryDto rtRow(
            Long animeId, String title, String cover, Long trending, Long popularity
    ) {
        // 게터만 공개되어 있고 @AllArgsConstructor가 없으니
        // 테스트에서 필드 세팅 가능한 동일 패키지 보조 DTO를 만들거나
        // 간단히 익명 서브클래스를 사용한다. (여기선 익명 서브클래스)
        return new RealTimeRankingAnimesFromQueryDto() {
            public Long getAnimeId() { return animeId; }
            public String getTitle() { return title; }
            public String getCoverImageUrl() { return cover; }
            public Long getTrending() { return trending; }
            public Long getPopularity() { return popularity; }
        };
    }

    private RankingAnimesFromQueryDto seasonRow(
            Long animeId, String title, String cover, Long popularity
    ) {
        return new RankingAnimesFromQueryDto(animeId, title, cover, popularity);
    }

    private AnimeGenresDto genreOf(Long animeId, Long genreId, String name) {
        return new AnimeGenresDto(animeId, genreId, name);
    }
}