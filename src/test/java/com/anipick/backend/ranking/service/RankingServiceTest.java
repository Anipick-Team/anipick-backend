package com.anipick.backend.ranking.service;

import com.anipick.backend.ranking.dto.AnimeGenresDto;
import com.anipick.backend.ranking.dto.RankingAnimesFromQueryDto;
import com.anipick.backend.ranking.dto.RankingResponse;
import com.anipick.backend.ranking.mapper.RankingMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {
    @Mock
    private RankingMapper rankingMapper;

    @InjectMocks
    private RankingService rankingService;

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