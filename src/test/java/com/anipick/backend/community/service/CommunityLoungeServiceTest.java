package com.anipick.backend.community.service;

import com.anipick.backend.anime.domain.Day;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.community.dto.LoungePageDto;
import com.anipick.backend.community.dto.LoungeSeriesItemDto;
import com.anipick.backend.community.dto.LoungeSeriesRawDto;
import com.anipick.backend.community.mapper.CommunityLoungeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.DayOfWeek;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class CommunityLoungeServiceTest {

    @Mock
    CommunityLoungeMapper communityLoungeMapper;

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    HashOperations<String, Object, Object> hashOps;

    CommunityLoungeService sut;

    @BeforeEach
    void setUp() {
        sut = new CommunityLoungeService(communityLoungeMapper, redisTemplate, new ObjectMapper());
    }

    private void givenSchedule(Day day, String json) {
        given(redisTemplate.opsForHash()).willReturn(hashOps);
        given(hashOps.get("schedule:week:current", day.name())).willReturn(json);
    }

    private static String schedule(long animeId, int episode, int popularityRank) {
        return "{\"animeId\":%d,\"episode\":%d,\"popularityRank\":%d}".formatted(animeId, episode, popularityRank);
    }

    private static LoungeSeriesRawDto series(long animeId, long seriesId) {
        return new LoungeSeriesRawDto(animeId, seriesId, null, "시리즈" + seriesId, null, null, null, "cover" + seriesId);
    }

    @Test
    @DisplayName("인기 순위 순으로 정렬하고, 같은 시리즈는 순위가 가장 높은 애니의 회차로 하나만 남긴다")
    void mergesSameSeriesByBestRank() {
        // 애니 10, 11 은 시리즈 100 소속. 11 의 순위가 더 높다
        givenSchedule(Day.MON, "[" + String.join(",",
                schedule(10, 5, 3),
                schedule(11, 2, 1),
                schedule(20, 7, 2)) + "]");
        given(communityLoungeMapper.selectSeriesByAnimeIds(anyList()))
                .willReturn(List.of(series(10, 100), series(11, 100), series(20, 200)));

        LoungePageDto result = sut.getLounge(Day.MON, null, 18);

        assertThat(result.getCount()).isEqualTo(2L);
        assertThat(result.getSeries()).extracting(LoungeSeriesItemDto::getSeriesId).containsExactly(100L, 200L);
        assertThat(result.getSeries().get(0).getEpisode()).isEqualTo(2);
        assertThat(result.getSeries().get(0).getTitle()).isEqualTo("시리즈100");
        assertThat(result.getCursor().getLastId()).isEqualTo(200L);
    }

    @Test
    @DisplayName("시리즈 매핑이 없는(또는 성인 애니라 매퍼가 돌려주지 않은) 애니는 제외한다")
    void excludesUnmappedAnime() {
        givenSchedule(Day.MON, "[" + String.join(",",
                schedule(10, 1, 1),
                schedule(30, 1, 2)) + "]");
        given(communityLoungeMapper.selectSeriesByAnimeIds(anyList()))
                .willReturn(List.of(series(30, 300)));

        LoungePageDto result = sut.getLounge(Day.MON, null, 18);

        assertThat(result.getSeries()).extracting(LoungeSeriesItemDto::getSeriesId).containsExactly(300L);
    }

    @Test
    @DisplayName("lastId(seriesId) 다음 위치부터 size 개를 자른다")
    void pagesAfterLastId() {
        givenSchedule(Day.MON, "[" + String.join(",",
                schedule(1, 1, 1),
                schedule(2, 1, 2),
                schedule(3, 1, 3)) + "]");
        given(communityLoungeMapper.selectSeriesByAnimeIds(anyList()))
                .willReturn(List.of(series(1, 101), series(2, 102), series(3, 103)));

        LoungePageDto result = sut.getLounge(Day.MON, 101L, 1);

        assertThat(result.getCount()).isEqualTo(3L);
        assertThat(result.getSeries()).extracting(LoungeSeriesItemDto::getSeriesId).containsExactly(102L);
        assertThat(result.getCursor().getLastId()).isEqualTo(102L);
    }

    @Test
    @DisplayName("마지막 페이지 다음 요청이면 빈 목록과 lastId null 커서를 준다")
    void emptyAfterLastPage() {
        givenSchedule(Day.MON, "[" + schedule(1, 1, 1) + "]");
        given(communityLoungeMapper.selectSeriesByAnimeIds(anyList()))
                .willReturn(List.of(series(1, 101)));

        LoungePageDto result = sut.getLounge(Day.MON, 101L, 18);

        assertThat(result.getSeries()).isEmpty();
        assertThat(result.getCursor().getLastId()).isNull();
    }

    @Test
    @DisplayName("Redis 에 오늘 데이터가 없으면 DB 를 조회하지 않고 빈 결과를 준다")
    void emptyWhenNoRedisData() {
        givenSchedule(Day.TUE, null);

        LoungePageDto result = sut.getLounge(Day.TUE, null, 18);

        assertThat(result.getCount()).isZero();
        assertThat(result.getSeries()).isEmpty();
        verify(communityLoungeMapper, never()).selectSeriesByAnimeIds(anyList());
    }

    @Test
    @DisplayName("size 가 0 이하이면 Redis·DB 를 조회하지 않고 BAD_REQUEST")
    void sizeValidation() {
        assertThatThrownBy(() -> sut.getLounge(Day.MON, null, 0))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.BAD_REQUEST);
        assertThatThrownBy(() -> sut.getLounge(Day.MON, null, -1))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.BAD_REQUEST);

        verifyNoInteractions(redisTemplate, communityLoungeMapper);
    }

    @Test
    @DisplayName("size 가 int 최댓값이어도 오버플로 없이 lastId 다음 항목을 모두 준다")
    void hugeSizeNoOverflow() {
        givenSchedule(Day.MON, "[" + String.join(",",
                schedule(1, 1, 1),
                schedule(2, 1, 2),
                schedule(3, 1, 3)) + "]");
        given(communityLoungeMapper.selectSeriesByAnimeIds(anyList()))
                .willReturn(List.of(series(1, 101), series(2, 102), series(3, 103)));

        LoungePageDto result = sut.getLounge(Day.MON, 101L, Integer.MAX_VALUE);

        assertThat(result.getSeries()).extracting(LoungeSeriesItemDto::getSeriesId).containsExactly(102L, 103L);
    }

    @Test
    @DisplayName("Redis JSON 파싱에 실패하면 DB 를 조회하지 않고 빈 결과를 준다")
    void emptyWhenJsonBroken() {
        givenSchedule(Day.MON, "{not-json");

        LoungePageDto result = sut.getLounge(Day.MON, null, 18);

        assertThat(result.getCount()).isZero();
        assertThat(result.getSeries()).isEmpty();
        assertThat(result.getCursor().getLastId()).isNull();
        verify(communityLoungeMapper, never()).selectSeriesByAnimeIds(anyList());
    }

    @Test
    @DisplayName("popularityRank 가 없는 애니는 순위가 있는 애니 뒤로 보낸다")
    void nullRankGoesLast() {
        givenSchedule(Day.MON, "[" + String.join(",",
                "{\"animeId\":1,\"episode\":1,\"popularityRank\":null}",
                schedule(2, 1, 5)) + "]");
        given(communityLoungeMapper.selectSeriesByAnimeIds(anyList()))
                .willReturn(List.of(series(1, 101), series(2, 102)));

        LoungePageDto result = sut.getLounge(Day.MON, null, 18);

        assertThat(result.getSeries()).extracting(LoungeSeriesItemDto::getSeriesId).containsExactly(102L, 101L);
    }

    @Test
    @DisplayName("lastId 가 목록에 없으면(요일이 바뀌었거나 잘못된 커서) 처음부터 준다")
    void unknownLastIdStartsFromBeginning() {
        givenSchedule(Day.MON, "[" + String.join(",",
                schedule(1, 1, 1),
                schedule(2, 1, 2)) + "]");
        given(communityLoungeMapper.selectSeriesByAnimeIds(anyList()))
                .willReturn(List.of(series(1, 101), series(2, 102)));

        LoungePageDto result = sut.getLounge(Day.MON, 999L, 1);

        assertThat(result.getSeries()).extracting(LoungeSeriesItemDto::getSeriesId).containsExactly(101L);
        assertThat(result.getCursor().getLastId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("java DayOfWeek 를 Day enum 으로 변환한다")
    void toDay() {
        assertThat(CommunityLoungeService.toDay(DayOfWeek.MONDAY)).isEqualTo(Day.MON);
        assertThat(CommunityLoungeService.toDay(DayOfWeek.SUNDAY)).isEqualTo(Day.SUN);
    }
}
