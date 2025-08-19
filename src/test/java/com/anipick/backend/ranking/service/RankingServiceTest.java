package com.anipick.backend.ranking.service;

import com.anipick.backend.anime.dto.GenreDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.ranking.dto.*;
import com.anipick.backend.ranking.mapper.RankingMapper;
import com.anipick.backend.ranking.mapper.RealTimeRankingMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Mock
    private RankingMapper rankingMapper;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private RealTimeRankingMapper realTimeRankingMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RankingService rankingService;

    private static final String RANKING_ALIAS_KEY = "anime_real_rank:";

    private void stubRedisAliasAndPayload(String genre, String realKey, String payloadJson) {
        String aliasKey = RANKING_ALIAS_KEY + genre;
        // valueOperations.get() 호출 순서에 상관없이 키-값 대응
        given(valueOperations.get(eq(aliasKey))).willReturn(realKey);
        given(valueOperations.get(eq(realKey))).willReturn(payloadJson);
    }

    private RedisRealTimeRankingAnimesDto newRedisDto(long rank, long change, String trend, long animeId) {
        try {
            RedisRealTimeRankingAnimesDto dto = new RedisRealTimeRankingAnimesDto(); // 기본 생성자 사용
            set(dto, "rank", rank);
            set(dto, "change", change);
            set(dto, "trend", trend);
            set(dto, "animeId", animeId);

            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void set(Object target, String field, Object value) throws Exception {
        var f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    private List<RedisRealTimeRankingAnimesDto> redisDtosWithVarChanges() {
        return List.of(
                newRedisDto(1,  3,  "up",   100),
                newRedisDto(2,  0,  "same", 101),
                newRedisDto(3, -2,  "down", 102),
                newRedisDto(4,  1,  "up",   103),
                newRedisDto(5,  0,  "same", 104)
        );
    }

    private Map<String, Long> newGenreMap() {
        Map<String, Long> genreMap = new HashMap<>();
        for(AnimeGenresDto dto : generateAnimeGenres()) {
            genreMap.putIfAbsent(dto.getGenreName(), dto.getGenreId());
        }

        return genreMap;
    }

    private RealTimeRankingAnimesFromQueryDto mockDbDto(long animeId, String title, String cover, List<String> genreNames) throws Exception {
        RealTimeRankingAnimesFromQueryDto dto = new RealTimeRankingAnimesFromQueryDto();
        set(dto, "animeId", animeId);
        set(dto, "title", title);
        set(dto, "coverImageUrl", cover);
        Map<String, Long> genreMap = newGenreMap();

        List<GenreDto> genres = genreNames.stream()
                .map(name -> {
                    Long genreId = genreMap.get(name);
                    return new GenreDto(genreId, name);
                }).toList();
        set(dto, "genres", genres);

        return dto;
    }

    @SuppressWarnings("unchecked")
    private void stubJacksonReadValueList(List<RedisRealTimeRankingAnimesDto> list) throws Exception {
        given(objectMapper.readValue(anyString(), any(TypeReference.class)))
                .willAnswer(inv -> list);
    }

    @Test
    @DisplayName("첫번째 페이지 - lastId = null일 때 랭킹 조회")
    void firstPage_lastIdNull_sizeLimit_and_fieldMapping_and_jsonIsCaptured() throws Exception {
        // given
        String genre = "액션";
        Integer size = 3;
        Long lastId = null;

        String realKey = "rt:액션";
        String payloadJson =
                "[" +
                        "{\"rank\":1,\"change\":3,\"trend\":\"up\",\"animeId\":100}," +
                        "{\"rank\":2,\"change\":0,\"trend\":\"same\",\"animeId\":101}," +
                        "{\"rank\":3,\"change\":-2,\"trend\":\"down\",\"animeId\":102}," +
                        "{\"rank\":4,\"change\":1,\"trend\":\"up\",\"animeId\":103}," +
                        "{\"rank\":5,\"change\":0,\"trend\":\"same\",\"animeId\":104}" +
                "]";

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        stubRedisAliasAndPayload(genre, realKey, payloadJson);

        // Redis 리스트(실제 파싱 결과)는 상위 5개로 가정하되 size=3만 취함
        List<RedisRealTimeRankingAnimesDto> redisList = redisDtosWithVarChanges();
        stubJacksonReadValueList(redisList);

        // DB는 상위 3개 animeIds = [100,101,102]
        List<Long> expectedIds = List.of(100L, 101L, 102L);
        List<RealTimeRankingAnimesFromQueryDto> dbList = List.of(
                mockDbDto(100L, "A1", "cover1", List.of("액션", "모험")),
                mockDbDto(101L, "A2", "cover2", List.of("모험")),
                mockDbDto(102L, "A3", "cover3", List.of("판타지", "액션", "스릴러"))
        );
        given(realTimeRankingMapper.getRealTimeRanking(expectedIds, genre)).willReturn(dbList);

        // objectMapper 인자 캡처
        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);

        // when
        RealTimeRankingResponse resp = rankingService.getRealTimeRanking(genre, lastId, size);

        // then
        assertThat(resp).isNotNull();
        assertThat(resp.getAnimes()).hasSize(3);

        // 1위(up, +3)
        RealTimeRankingAnimesDto r1 = resp.getAnimes().get(0);
        assertThat(r1.getAnimeId()).isEqualTo(100L);
        assertThat(r1.getTitle()).isEqualTo("A1");
        assertThat(r1.getCoverImageUrl()).isEqualTo("cover1");
        assertThat(r1.getRank()).isEqualTo(1L);
        assertThat(r1.getChange()).isEqualTo(3L);
        assertThat(r1.getTrend()).isEqualTo("up");
        assertThat(r1.getGenres()).containsExactlyInAnyOrder("액션", "모험");

        // 2위(same, 0)
        RealTimeRankingAnimesDto r2 = resp.getAnimes().get(1);
        assertThat(r2.getAnimeId()).isEqualTo(101L);
        assertThat(r2.getRank()).isEqualTo(2L);
        assertThat(r2.getChange()).isEqualTo(0L);
        assertThat(r2.getTrend()).isEqualTo("same");

        // 3위(down, -2) + 장르 3개 제한은 다른 테스트에서 별도 검증
        RealTimeRankingAnimesDto r3 = resp.getAnimes().get(2);
        assertThat(r3.getAnimeId()).isEqualTo(102L);
        assertThat(r3.getRank()).isEqualTo(3L);
        assertThat(r3.getChange()).isEqualTo(-2L);
        assertThat(r3.getTrend()).isEqualTo("down");

        // objectMapper가 우리가 넣은 payloadJson으로 호출되었는지 확인
        verify(objectMapper).readValue(jsonCaptor.capture(), any(TypeReference.class));
        assertThat(jsonCaptor.getValue()).isEqualTo(payloadJson);

        // mapper 호출 인수/순서 검증
        ArgumentCaptor<List<Long>> idsCaptor = ArgumentCaptor.forClass(List.class);
        verify(realTimeRankingMapper).getRealTimeRanking(idsCaptor.capture(), eq(genre));
        assertThat(idsCaptor.getValue()).containsExactlyElementsOf(expectedIds);
    }

    @Test
    @DisplayName("다음 페이지 - lastId != null일 때 랭킹 조회")
    void nextPage_lastIdExists_filters_rank_gt_lastId_and_limits_and_trendDownRemains() throws Exception {
        // given
        String genre = "드라마";
        Integer size = 2;
        Long lastId = 2L; // rank > 2만

        String realKey = "rt:드라마";
        String payloadJson =
                "[" +
                        "{\"rank\":1,\"change\":3,\"trend\":\"up\",\"animeId\":100}," +
                        "{\"rank\":2,\"change\":0,\"trend\":\"same\",\"animeId\":101}," +
                        "{\"rank\":3,\"change\":-2,\"trend\":\"down\",\"animeId\":102}," +
                        "{\"rank\":4,\"change\":1,\"trend\":\"up\",\"animeId\":103}," +
                        "{\"rank\":5,\"change\":0,\"trend\":\"same\",\"animeId\":104}" +
                "]";

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        stubRedisAliasAndPayload(genre, realKey, payloadJson);

        List<RedisRealTimeRankingAnimesDto> redisList = redisDtosWithVarChanges();
        stubJacksonReadValueList(redisList);

        // 필터 후 rank=3,4 → animeIds = [102,103]
        List<RealTimeRankingAnimesFromQueryDto> dbList = List.of(
                mockDbDto(102L, "B1", "c1", List.of("드라마")),
                mockDbDto(103L, "B2", "c2", List.of("드라마"))
        );
        given(realTimeRankingMapper.getRealTimeRanking(List.of(102L, 103L), genre))
                .willReturn(dbList);

        // when
        RealTimeRankingResponse response = rankingService.getRealTimeRanking(genre, lastId, size);

        // then
        assertThat(response.getAnimes()).hasSize(2);

        RealTimeRankingAnimesDto a = response.getAnimes().get(0); // rank=3
        assertThat(a.getRank()).isEqualTo(3L);
        assertThat(a.getChange()).isEqualTo(-2L);
        assertThat(a.getTrend()).isEqualTo("down");

        RealTimeRankingAnimesDto b = response.getAnimes().get(1); // rank=4
        assertThat(b.getRank()).isEqualTo(4L);
        assertThat(b.getChange()).isEqualTo(1L);
        assertThat(b.getTrend()).isEqualTo("up");
    }

    @Test
    @DisplayName("Genre = null일 때 랭킹 조회")
    void genreNull_is_passed_and_aliasKey_uses_null_suffix() throws Exception {
        // given
        String genre = null; // 장르 없음
        Integer size = 2;
        Long lastId = null;

        String realKey = "rt:null";
        String payloadJson =
                "[" +
                        "{\"rank\":1,\"change\":0,\"trend\":\"same\",\"animeId\":100}," +
                        "{\"rank\":2,\"change\":1,\"trend\":\"up\",\"animeId\":101}," +
                        "{\"rank\":3,\"change\":-1,\"trend\":\"down\",\"animeId\":102}" +
                        "]";

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        // alias key: "anime_real_rank:null"
        String aliasKey = RANKING_ALIAS_KEY + genre; // 장르가 null이면 "anime_real_rank:null" 문자열이 실제 키로 저장되는 정책이라 가정
        given(valueOperations.get(aliasKey)).willReturn(realKey);
        given(valueOperations.get(realKey)).willReturn(payloadJson);

        List<RedisRealTimeRankingAnimesDto> redisList = List.of(
                newRedisDto(1, 0, "same", 100),
                newRedisDto(2, 1, "up", 101),
                newRedisDto(3, -1, "down", 102)
        );
        stubJacksonReadValueList(redisList);

        // size=2 → animeIds=[100,101], genre=null 전달
        List<RealTimeRankingAnimesFromQueryDto> dbList = List.of(
                mockDbDto(100L, "N1", "c1", List.of("기타")),
                mockDbDto(101L, "N2", "c2", List.of("기타2"))
        );
        given(realTimeRankingMapper.getRealTimeRanking(List.of(100L, 101L), genre)).willReturn(dbList);

        // when
        RealTimeRankingResponse response = rankingService.getRealTimeRanking(genre, lastId, size);

        // then
        assertThat(response.getAnimes()).hasSize(2);
        verify(realTimeRankingMapper).getRealTimeRanking(List.of(100L, 101L), null);
    }

    @Test
    @DisplayName("Genre 상위 3개만 조회")
    void genres_are_limited_to_top3_in_from_mapping() throws Exception {
        // given
        String genre = "액션";
        Integer size = 1;
        Long lastId = null;

        String realKey = "rt:액션";
        String payloadJson = "[{\"rank\":1,\"change\":0,\"trend\":\"same\",\"animeId\":100}]";

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        stubRedisAliasAndPayload(genre, realKey, payloadJson);

        List<RedisRealTimeRankingAnimesDto> redisList = List.of(newRedisDto(1, 0, "same", 100));
        stubJacksonReadValueList(redisList);

        // 4개 장르지만 응답에는 3개만
        List<RealTimeRankingAnimesFromQueryDto> dbList = List.of(
                mockDbDto(100L, "A1", "c1", List.of("액션", "모험", "판타지", "개그"))
        );
        given(realTimeRankingMapper.getRealTimeRanking(List.of(100L), genre)).willReturn(dbList);

        // when
        RealTimeRankingResponse resp = rankingService.getRealTimeRanking(genre, lastId, size);

        // then
        assertThat(resp.getAnimes()).hasSize(1);
        RealTimeRankingAnimesDto item = resp.getAnimes().get(0);
        assertThat(item.getGenres()).hasSize(3)
                .containsExactly("액션", "모험", "판타지"); // 입력 순서 상위 3개
        assertThat(item.getTrend()).isEqualTo("same");
        assertThat(item.getChange()).isEqualTo(0L);
    }

    @Test
    @DisplayName("JSON에 이상한 값이 들어갔을 때 예외가 발생해야 한다.")
    void jsonParseError_is_wrapped_into_CustomException_INTERNAL_SERVER_ERROR() throws Exception {
        // given
        String genre = "스릴러";
        Integer size = 10;

        String realKey = "rt:thrill";
        String badJson = "NOT_JSON";

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        stubRedisAliasAndPayload(genre, realKey, badJson);

        // readValue 중 JsonProcessingException 발생
        given(objectMapper.readValue(anyString(), any(TypeReference.class)))
                .willThrow(new JsonProcessingException("boom") {});

        // when & then
        assertThatThrownBy(() ->
                        rankingService.getRealTimeRanking(genre, null, size)
                ).isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("연도/분기 랭킹 테스트 - 장르가 null일 때(장르 필터링 X - default)")
    void getYearSeasonRankingWhenGenreIsNull() throws JsonProcessingException {
        // given
        Integer year = 2025;
        Integer season = 1;
        String genre = null;
        Long lastId = null;
        Long newLastId = 20L;
        Integer size = 20;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<RankingAnimesFromQueryDto> yearSeasonRanking = generateTodayRanking().stream()
                .limit(20)
                .toList();
        List<RankingAnimesFromQueryDto> lastRanking = generateTodayRanking().stream()
                .filter(anime -> anime.getAnimeId().equals(21L))
                .toList();

        System.out.println("lastRanking size : " + lastRanking.size());

        List<RankingAnimesFromQueryDto> yesterdayRanking = generateYesterdayRanking();
        List<AnimeGenresDto> genres = generateAnimeGenres();
        List<Long> animeIds = yearSeasonRanking.stream()
                .map(RankingAnimesFromQueryDto::getAnimeId)
                .toList();
        List<Long> lastAnimeIds = lastRanking.stream()
                .map(RankingAnimesFromQueryDto::getAnimeId)
                .toList();

        // when
        // genre = null && lastId = null -> first paging
        when(rankingMapper.getYearSeasonRankingNotFilterPaging(year, season, lastId, size, today))
                .thenReturn(yearSeasonRanking);

        when(rankingMapper.getYearSeasonRankingNotFilter(year, season, yesterday))
                .thenReturn(yesterdayRanking);

        when(rankingMapper.getGenresByAnimeIds(animeIds))
                .thenReturn(genres);

        RankingResponse yearSeasonRankingWhenGenreIsNullAtFirstPage = rankingService.getYearSeasonRanking(year, season, genre, lastId, size);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(yearSeasonRankingWhenGenreIsNullAtFirstPage);
        System.out.println(json);

        // genre = null && lastId = 20L -> second paging
        when(rankingMapper.getYearSeasonRankingNotFilterPaging(year, season, newLastId, size, today))
                .thenReturn(lastRanking);

        when(rankingMapper.getYearSeasonRankingNotFilter(year, season, yesterday))
                .thenReturn(yesterdayRanking);

        when(rankingMapper.getGenresByAnimeIds(lastAnimeIds))
                .thenReturn(genres);

        RankingResponse yearSeasonRankingWhenGenreIsNullAtSecondPage = rankingService.getYearSeasonRanking(year, season, genre, newLastId, size);
        String json2 = mapper.writeValueAsString(yearSeasonRankingWhenGenreIsNullAtSecondPage);
        System.out.println(json2);

        // then
        // ranking은 null이 아니어야 한다.
        assertNotNull(yearSeasonRankingWhenGenreIsNullAtFirstPage);

        // ranking은 20위까지 있어야 한다.
        assertEquals(20, yearSeasonRankingWhenGenreIsNullAtFirstPage.getAnimes().size());

        // lastId는 20여야 한다.
        assertEquals(20, yearSeasonRankingWhenGenreIsNullAtFirstPage.getCursor().getLastId());

        // 2번째 페이지 랭킹은 1개여야 한다.
        assertEquals(1,  yearSeasonRankingWhenGenreIsNullAtSecondPage.getAnimes().size());
    }

    @Test
    @DisplayName("연도/분기 랭킹 테스트 - 장르가 존재할 때")
    void getYearSeasonRankingWhenGenreIsNotNull() throws JsonProcessingException {
        // given
        Integer year = 2025;
        Integer season = 1;
        String genre = "액션";
        Long lastId = null;
        Long newLastId = 20L;
        Integer size = 20;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        ObjectMapper mapper = new ObjectMapper();

        List<RankingAnimesFromQueryDto> yearSeasonRanking = generateTodayRanking().stream()
                .limit(20)
                .toList();
        List<RankingAnimesFromQueryDto> lastRanking = generateTodayRanking().stream()
                .filter(anime -> anime.getAnimeId().equals(21L))
                .toList();

        System.out.println("lastRanking size : " + lastRanking.size());

        List<RankingAnimesFromQueryDto> yesterdayRanking = generateYesterdayRanking();
        List<AnimeGenresDto> genres = generateAnimeGenres().stream()
                .filter(g -> !g.getAnimeId().equals(21L))
                .toList();
        List<Long> actionGenreAnimeIds = genres.stream()
                .filter(g -> g.getGenreName().equals(genre))
                .map(AnimeGenresDto::getAnimeId)
                .toList();
        List<AnimeGenresDto> actionGenres = genres.stream()
                .filter(g -> actionGenreAnimeIds.contains(g.getAnimeId()))
                .toList();

        System.out.println("actionGenres : " + mapper.writeValueAsString(actionGenres));
        System.out.println("actionGenreAnimeIds : " + actionGenreAnimeIds.toString());

        List<RankingAnimesFromQueryDto> yearSeasonRankingWhenGenreIsAction = actionGenreAnimeIds.stream()
                .map(animeId -> {
                    return yearSeasonRanking.stream()
                            .filter(dto -> dto.getAnimeId().equals(animeId))
                            .findFirst()
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();

        System.out.println("yearSeasonRankingWhenGenreIsAction size : " + yearSeasonRankingWhenGenreIsAction.size());

        // when
        // genre = "액션" && lastId = null -> first paging
        when(rankingMapper.getYearSeasonRankingByGenrePaging(year, season, genre, lastId, size, today))
                .thenReturn(yearSeasonRankingWhenGenreIsAction);

        when(rankingMapper.getYearSeasonRankingByGenre(year, season, genre, yesterday))
                .thenReturn(yesterdayRanking);

        when(rankingMapper.getGenresByAnimeIds(actionGenreAnimeIds))
                .thenReturn(actionGenres);

        RankingResponse yearSeasonRankingWhenGenreIsNotNullAtFirstPage = rankingService.getYearSeasonRanking(year, season, genre, lastId, size);
        String json = mapper.writeValueAsString(yearSeasonRankingWhenGenreIsNotNullAtFirstPage);
        System.out.println(json);

        // then
        // ranking은 null이 아니어야 한다.
        assertNotNull(yearSeasonRankingWhenGenreIsNotNullAtFirstPage);

        // 장르가 액션인 것들 size는 6과 동일해야 한다.
        assertEquals(6, yearSeasonRankingWhenGenreIsNotNullAtFirstPage.getAnimes().size());

        // lastId는 6이어야 한다.
        assertEquals(6, yearSeasonRankingWhenGenreIsNotNullAtFirstPage.getCursor().getLastId());

        //랭킹의 마지막 애니에 표시되는 장르의 첫번째 순서는 액션이어야 한다.
        assertEquals("액션", yearSeasonRankingWhenGenreIsNotNullAtFirstPage.getAnimes().getLast().getGenres().getFirst());
    }

    @Test
    @DisplayName("역대 랭킹 테스트 - 장르가 존재하지 않을 때")
    void getAllTimeRankingWhenGenreIsNull() throws JsonProcessingException {
        //given
        String genre = null;
        Long lastId = null;
        Long newLastId = 20L;
        Integer size = 20;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<RankingAnimesFromQueryDto> allTimeRanking = generateTodayRanking().stream()
                .limit(20)
                .toList();
        List<RankingAnimesFromQueryDto> lastRanking = generateTodayRanking().stream()
                .filter(anime -> anime.getAnimeId().equals(21L))
                .toList();

        System.out.println("lastRanking size : " + lastRanking.size());

        List<RankingAnimesFromQueryDto> yesterdayRanking = generateYesterdayRanking();
        List<AnimeGenresDto> genres = generateAnimeGenres();
        List<Long> animeIds = allTimeRanking.stream()
                .map(RankingAnimesFromQueryDto::getAnimeId)
                .toList();
        List<Long> lastAnimeIds = lastRanking.stream()
                .map(RankingAnimesFromQueryDto::getAnimeId)
                .toList();

        // when
        // genre = null && lastId = null -> first page
        when(rankingMapper.getAllTimeRankingNotFilterPaging(lastId, size, today))
                .thenReturn(allTimeRanking);

        when(rankingMapper.getAllTimeRankingNotFilter(yesterday))
                .thenReturn(yesterdayRanking);

        when(rankingMapper.getGenresByAnimeIds(animeIds))
                .thenReturn(genres);

        RankingResponse allTimeRankingWhenGenreIsNullAtFirstPage = rankingService.getAllTimeRanking(genre, lastId, size);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(allTimeRankingWhenGenreIsNullAtFirstPage);
        System.out.println(json);

        // genre = null && lastId = 20L -> second page
        when(rankingMapper.getAllTimeRankingNotFilterPaging(newLastId, size, today))
                .thenReturn(lastRanking);

        when(rankingMapper.getAllTimeRankingNotFilter(yesterday))
                .thenReturn(yesterdayRanking);

        when(rankingMapper.getGenresByAnimeIds(lastAnimeIds))
                .thenReturn(genres);

        RankingResponse allTimeRankingWhenGenreIsNullAtSecondPage = rankingService.getAllTimeRanking(genre, newLastId, size);
        String json2 = mapper.writeValueAsString(allTimeRankingWhenGenreIsNullAtSecondPage);
        System.out.println(json2);

        //then
        assertNotNull(allTimeRankingWhenGenreIsNullAtFirstPage);

        assertEquals(20, allTimeRankingWhenGenreIsNullAtFirstPage.getAnimes().size());

        assertEquals(20, allTimeRankingWhenGenreIsNullAtFirstPage.getCursor().getLastId());

        assertEquals(1, allTimeRankingWhenGenreIsNullAtSecondPage.getAnimes().size());
    }

    @Test
    @DisplayName("역대 랭킹 테스트 - 장르가 존재할 때")
    void getAllTimeRankingWhenGenreIsNotNull() throws JsonProcessingException {
        String genre = "액션";
        Long lastId = null;
        Long newLastId = 20L;
        Integer size = 20;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        ObjectMapper mapper = new ObjectMapper();

        List<RankingAnimesFromQueryDto> allTimeRanking = generateTodayRanking().stream()
                .limit(20)
                .toList();
        List<RankingAnimesFromQueryDto> lastRanking = generateTodayRanking().stream()
                .filter(anime -> anime.getAnimeId().equals(21L))
                .toList();

        System.out.println("lastRanking size : " + lastRanking.size());

        List<RankingAnimesFromQueryDto> yesterdayRanking = generateYesterdayRanking();
        List<AnimeGenresDto> genres = generateAnimeGenres().stream()
                .filter(g -> !g.getAnimeId().equals(21L))
                .toList();
        List<Long> actionGenreAnimeIds = genres.stream()
                .filter(g -> g.getGenreName().equals(genre))
                .map(AnimeGenresDto::getAnimeId)
                .toList();
        List<AnimeGenresDto> actionGenres = genres.stream()
                .filter(g -> actionGenreAnimeIds.contains(g.getAnimeId()))
                .toList();

        System.out.println("actionGenres : " + mapper.writeValueAsString(actionGenres));
        System.out.println("actionGenreAnimeIds : " + actionGenreAnimeIds.toString());

        List<RankingAnimesFromQueryDto> allTimeRankingWhenGenreIsAction = actionGenreAnimeIds.stream()
                .map(animeId -> {
                    return allTimeRanking.stream()
                            .filter(dto -> dto.getAnimeId().equals(animeId))
                            .findFirst()
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();

        System.out.println("allTimeRankingWhenGenreIsAction size : " + allTimeRankingWhenGenreIsAction.size());

        // when
        // genre = "액션" && lastId = null -> first paging
        when(rankingMapper.getAllTimeRankingByGenrePaging(genre, lastId, size, today))
                .thenReturn(allTimeRankingWhenGenreIsAction);

        when(rankingMapper.getAllTimeRankingByGenre(genre, yesterday))
                .thenReturn(yesterdayRanking);

        when(rankingMapper.getGenresByAnimeIds(actionGenreAnimeIds))
                .thenReturn(actionGenres);

        RankingResponse allTimeRankingWhenGenreIsNotNullAtFirstPage = rankingService.getAllTimeRanking(genre, lastId, size);
        String json = mapper.writeValueAsString(allTimeRankingWhenGenreIsNotNullAtFirstPage);
        System.out.println(json);

        assertNotNull(allTimeRankingWhenGenreIsNotNullAtFirstPage);

        assertEquals(6, allTimeRankingWhenGenreIsNotNullAtFirstPage.getAnimes().size());

        assertEquals(6, allTimeRankingWhenGenreIsNotNullAtFirstPage.getCursor().getLastId());

        assertEquals("액션", allTimeRankingWhenGenreIsNotNullAtFirstPage.getAnimes().getLast().getGenres().getFirst());
    }

    @Test
    @DisplayName("연도/분기 랭킹 조회 결과가 0인 경우 빈 리스트를 반환해야 한다.")
    void ifGetYearSeasonRankingSizeIsZero_mustReturnEmptyList() throws JsonProcessingException {
        Integer year = 2025;
        Integer season = 1;
        String genre = null;
        Long lastId = null;
        Long newLastId = 20L;
        Integer size = 20;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        ObjectMapper mapper = new ObjectMapper();

        List<RankingAnimesFromQueryDto> yearSeasonRanking = List.of();
        List<RankingAnimesFromQueryDto> yesterdayRanking = List.of();
        List<AnimeGenresDto> genres = List.of();

        when(rankingMapper.getYearSeasonRankingNotFilterPaging(year, season, lastId, size, today))
                .thenReturn(yearSeasonRanking);

        when(rankingMapper.getYearSeasonRankingNotFilter(year, season, yesterday))
                .thenReturn(yesterdayRanking);

        when(rankingMapper.getGenresByAnimeIds(List.of()))
                .thenReturn(genres);

        RankingResponse response = rankingService.getYearSeasonRanking(year, season, genre, lastId, size);
        String json = mapper.writeValueAsString(response);
        System.out.println(json);

        assertEquals(0, response.getAnimes().size());
    }

    @Test
    @DisplayName("역대 랭킹 조회 결과가 0인 경우 빈 리스트를 반환해야 한다.")
    void ifGetAllTimeRankingSizeIsZero_mustReturnEmptyList() throws JsonProcessingException {
        String genre = null;
        Long lastId = null;
        Long newLastId = 20L;
        Integer size = 20;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        ObjectMapper mapper = new ObjectMapper();

        List<RankingAnimesFromQueryDto> allTimeRanking = List.of();
        List<RankingAnimesFromQueryDto> yesterdayRanking = List.of();
        List<AnimeGenresDto> genres = List.of();

        when(rankingMapper.getAllTimeRankingNotFilterPaging(lastId, size, today))
                .thenReturn(allTimeRanking);

        when(rankingMapper.getAllTimeRankingNotFilter(yesterday))
                .thenReturn(yesterdayRanking);

        when(rankingMapper.getGenresByAnimeIds(List.of()))
                .thenReturn(genres);

        RankingResponse response = rankingService.getAllTimeRanking(genre, lastId, size);
        String json = mapper.writeValueAsString(response);
        System.out.println(json);

        assertEquals(0, response.getAnimes().size());
    }

    private List<RankingAnimesFromQueryDto> generateTodayRanking() {
        return List.of(
                new RankingAnimesFromQueryDto(1L, "Anime 1", "default.png", 1L),
                new RankingAnimesFromQueryDto(2L, "Anime 2", "default.png", 2L),
                new RankingAnimesFromQueryDto(3L, "Anime 3", "default.png", 3L),
                new RankingAnimesFromQueryDto(4L, "Anime 4", "default.png", 4L),
                new RankingAnimesFromQueryDto(5L, "Anime 5", "default.png", 5L),
                new RankingAnimesFromQueryDto(6L, "Anime 6", "default.png", 6L),
                new RankingAnimesFromQueryDto(7L, "Anime 7", "default.png", 7L),
                new RankingAnimesFromQueryDto(8L, "Anime 8", "default.png", 8L),
                new RankingAnimesFromQueryDto(9L, "Anime 9", "default.png", 9L),
                new RankingAnimesFromQueryDto(10L, "Anime 10", "default.png", 10L),
                new RankingAnimesFromQueryDto(11L, "Anime 11", "default.png", 11L),
                new RankingAnimesFromQueryDto(12L, "Anime 12", "default.png", 12L),
                new RankingAnimesFromQueryDto(13L, "Anime 13", "default.png", 13L),
                new RankingAnimesFromQueryDto(14L, "Anime 14", "default.png", 14L),
                new RankingAnimesFromQueryDto(15L, "Anime 15", "default.png", 15L),
                new RankingAnimesFromQueryDto(16L, "Anime 16", "default.png", 16L),
                new RankingAnimesFromQueryDto(17L, "Anime 17", "default.png", 17L),
                new RankingAnimesFromQueryDto(18L, "Anime 18", "default.png", 18L),
                new RankingAnimesFromQueryDto(19L, "Anime 19", "default.png", 19L),
                new RankingAnimesFromQueryDto(20L, "Anime 20", "default.png", 20L),
                new RankingAnimesFromQueryDto(21L, "Anime 21", "default.png", 21L)
        );
    }

    private List<RankingAnimesFromQueryDto> generateYesterdayRanking() {
        return List.of(
                new RankingAnimesFromQueryDto(1L, "Anime 1", "default.png", 11L),
                new RankingAnimesFromQueryDto(2L, "Anime 2", "default.png", 5L),
                new RankingAnimesFromQueryDto(3L, "Anime 3", "default.png", 17L),
                new RankingAnimesFromQueryDto(4L, "Anime 4", "default.png", 3L),
                new RankingAnimesFromQueryDto(5L, "Anime 5", "default.png", 14L),
                new RankingAnimesFromQueryDto(6L, "Anime 6", "default.png", 19L),
                new RankingAnimesFromQueryDto(7L, "Anime 7", "default.png", 1L),
                new RankingAnimesFromQueryDto(8L, "Anime 8", "default.png", 9L),
                new RankingAnimesFromQueryDto(9L, "Anime 9", "default.png", 20L),
                new RankingAnimesFromQueryDto(10L, "Anime 10", "default.png", 2L),
                new RankingAnimesFromQueryDto(11L, "Anime 11", "default.png", 18L),
                new RankingAnimesFromQueryDto(12L, "Anime 12", "default.png", 12L),
                new RankingAnimesFromQueryDto(13L, "Anime 13", "default.png", 15L),
                new RankingAnimesFromQueryDto(14L, "Anime 14", "default.png", 8L),
                new RankingAnimesFromQueryDto(15L, "Anime 15", "default.png", 6L),
                new RankingAnimesFromQueryDto(16L, "Anime 16", "default.png", 21L),
                new RankingAnimesFromQueryDto(17L, "Anime 17", "default.png", 16L),
                new RankingAnimesFromQueryDto(18L, "Anime 18", "default.png", 4L),
                new RankingAnimesFromQueryDto(19L, "Anime 19", "default.png", 7L),
                new RankingAnimesFromQueryDto(20L, "Anime 20", "default.png", 13L),
                new RankingAnimesFromQueryDto(21L, "Anime 21", "default.png", 10L)
        );
    }

    private List<AnimeGenresDto> generateAnimeGenres() {
        return List.of(
                new AnimeGenresDto(1L, 1L, "액션"),
                new AnimeGenresDto(1L, 2L, "모험"),
                new AnimeGenresDto(1L, 6L, "판타지"),
                new AnimeGenresDto(2L, 1L, "액션"),
                new AnimeGenresDto(2L, 14L, "로맨스"),
                new AnimeGenresDto(3L, 3L, "코미디"),
                new AnimeGenresDto(3L, 16L, "일상"),
                new AnimeGenresDto(4L, 4L, "드라마"),
                new AnimeGenresDto(4L, 5L, "섹시"),
                new AnimeGenresDto(5L, 6L, "판타지"),
                new AnimeGenresDto(5L, 9L, "마법소녀"),
                new AnimeGenresDto(6L, 14L, "로맨스"),
                new AnimeGenresDto(6L, 13L, "심리"),
                new AnimeGenresDto(7L, 1L, "액션"),
                new AnimeGenresDto(7L, 7L, "성인"),
                new AnimeGenresDto(7L, 8L, "공포"),
                new AnimeGenresDto(8L, 3L, "코미디"),
                new AnimeGenresDto(8L, 15L, "SF"),
                new AnimeGenresDto(9L, 12L, "미스터리"),
                new AnimeGenresDto(9L, 13L, "심리"),
                new AnimeGenresDto(10L, 11L, "음악"),
                new AnimeGenresDto(11L, 1L, "액션"),
                new AnimeGenresDto(11L, 10L, "메카"),
                new AnimeGenresDto(11L, 15L, "SF"),
                new AnimeGenresDto(12L, 4L, "드라마"),
                new AnimeGenresDto(12L, 14L, "로맨스"),
                new AnimeGenresDto(13L, 5L, "섹시"),
                new AnimeGenresDto(13L, 7L, "성인"),
                new AnimeGenresDto(14L, 16L, "일상"),
                new AnimeGenresDto(14L, 17L, "스포츠"),
                new AnimeGenresDto(15L, 6L, "판타지"),
                new AnimeGenresDto(15L, 1L, "액션"),
                new AnimeGenresDto(16L, 8L, "공포"),
                new AnimeGenresDto(17L, 18L, "초자연"),
                new AnimeGenresDto(18L, 12L, "미스터리"),
                new AnimeGenresDto(18L, 19L, "스릴러"),
                new AnimeGenresDto(19L, 13L, "심리"),
                new AnimeGenresDto(19L, 4L, "드라마"),
                new AnimeGenresDto(20L, 3L, "코미디"),
                new AnimeGenresDto(20L, 16L, "일상"),
                new AnimeGenresDto(20L, 1L, "액션"),
                new AnimeGenresDto(20L, 2L, "모험"),
                new AnimeGenresDto(21L, 1L, "액션")
        );
    }
}