package com.anipick.backend.ranking.service;

import com.anipick.backend.anime.dto.GenreDto;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.ranking.domain.RankingDefaults;
import com.anipick.backend.ranking.domain.Trend;
import com.anipick.backend.ranking.dto.*;
import com.anipick.backend.ranking.mapper.RankingMapper;
import com.anipick.backend.ranking.mapper.RealTimeRankingMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankingService {
    private final RankingMapper rankingMapper;
    private final RealTimeRankingMapper realTimeRankingMapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    public RealTimeRankingResponse getRealTimeRanking(String genre, Long lastId, Integer size) {
        try {
            String realTimeRankingKey = redisTemplate.opsForValue().get(RankingDefaults.RANKING_ALIAS_KEY + genre);
            String realTimeRankingJson = redisTemplate.opsForValue().get(realTimeRankingKey);

            List<RedisRealTimeRankingAnimesDto> redisAnimes = objectMapper.readValue(realTimeRankingJson, new TypeReference<List<RedisRealTimeRankingAnimesDto>>() {});
            List<RedisRealTimeRankingAnimesDto> slicedRedisRankingAnimes;

            if(lastId == null) {
                slicedRedisRankingAnimes = redisAnimes.stream()
                        .limit(size)
                        .toList();
            } else {
                slicedRedisRankingAnimes = redisAnimes.stream()
                        .filter(rank -> rank.getRank() > lastId)
                        .limit(size)
                        .toList();
            }

            List<Long> animeIds = slicedRedisRankingAnimes.stream()
                    .map(RedisRealTimeRankingAnimesDto::getAnimeId)
                    .toList();
            Map<Long, RedisRealTimeRankingAnimesDto> redisMap = slicedRedisRankingAnimes.stream()
                    .collect(Collectors.toMap(RedisRealTimeRankingAnimesDto::getAnimeId, dto -> dto));

            List<RealTimeRankingAnimesFromQueryDto> realTimeRanking = realTimeRankingMapper.getRealTimeRanking(animeIds, genre);
            List<RealTimeRankingAnimesDto> animes = realTimeRanking.stream()
                    .map(dto -> {
                        RedisRealTimeRankingAnimesDto redisData = redisMap.get(dto.getAnimeId());

                        return RealTimeRankingAnimesDto.from(dto.getAnimeId(), redisData.getRank(), redisData.getChange(), redisData.getTrend(), dto);
                    })
                    .toList();

            CursorDto cursor = CursorDto.of(lastId);

            return RealTimeRankingResponse.of(cursor, animes);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public RankingResponse getYearSeasonRanking(Integer year, Integer season, String genre, Long lastId, Integer size) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(RankingDefaults.ONE_DAY);

        List<RankingAnimesFromQueryDto> yearSeasonRankingToday = rankingMapper.getYearSeasonRankingPaging(year, season, genre, lastId, size, today);
        List<RankingAnimesFromQueryDto> yearSeasonRankingYesterday = rankingMapper.getYearSeasonRanking(year, season, genre, yesterday);
        List<Long> animeIds = yearSeasonRankingToday.stream()
                .map(RankingAnimesFromQueryDto::getAnimeId)
                .toList();

        List<AnimeGenresDto> genresByAnimeIds = rankingMapper.getGenresByAnimeIds(animeIds);
        Map<Long, List<AnimeGenresDto>> animeGenresMap = genresByAnimeIds.stream()
                .collect(Collectors.groupingBy(AnimeGenresDto::getAnimeId));
        Map<Long, Long> yesterdayRankMap =  yearSeasonRankingYesterday.stream()
                .collect(Collectors.toMap(
                        RankingAnimesFromQueryDto::getAnimeId,
                        RankingAnimesFromQueryDto::getRank
                ));
        List<RankingAnimesDto> animes;
        Long newLastId;
        AtomicReference<Long> displayRank = new AtomicReference<>(0L);

        if(genre != null) {
            animes = yearSeasonRankingToday.stream()
                    .map(dto -> {
                        Long todayRank = dto.getRank();
                        Long yesterdayRank = yesterdayRankMap.get(dto.getAnimeId());
                        displayRank.getAndSet(displayRank.get() + 1);
                        List<AnimeGenresDto> genres = animeGenresMap.get(dto.getAnimeId());

                        return calcAndMakeAnimeRanking(todayRank, yesterdayRank, displayRank.get(), dto, genres, genre);
                    })
                    .toList();
            newLastId = animes.getLast().getRank();
        } else {
            animes = yearSeasonRankingToday.stream()
                    .map(dto -> {
                        Long todayRank = dto.getRank();
                        Long yesterdayRank = yesterdayRankMap.get(dto.getAnimeId());
                        displayRank.set(todayRank);
                        List<AnimeGenresDto> genres = animeGenresMap.get(dto.getAnimeId());

                        return calcAndMakeAnimeRanking(todayRank, yesterdayRank, displayRank.get(), dto, genres, genre);
                    })
                    .toList();
            newLastId = animes.getLast().getRank();
        }

        CursorDto cursor = CursorDto.of(newLastId);

        return RankingResponse.of(cursor, animes);
    }

    public RankingResponse getAllTimeRanking(String genre, Long lastId, Integer size) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(RankingDefaults.ONE_DAY);

        List<RankingAnimesFromQueryDto> allTimeRankingToday = rankingMapper.getAllTimeRankingPaging(genre, lastId, size, today);
        List<RankingAnimesFromQueryDto> allTimeRankingYesterday = rankingMapper.getAllTimeRanking(genre, yesterday);
        List<Long> animeIds = allTimeRankingToday.stream()
                .map(RankingAnimesFromQueryDto::getAnimeId)
                .toList();

        List<AnimeGenresDto> genresByAnimeIds = rankingMapper.getGenresByAnimeIds(animeIds);
        Map<Long, List<AnimeGenresDto>> animeGenresMap = genresByAnimeIds.stream()
                .collect(Collectors.groupingBy(AnimeGenresDto::getAnimeId));

        Map<Long, Long> yesterdayRankMap = allTimeRankingYesterday.stream()
                .collect(Collectors.toMap(RankingAnimesFromQueryDto::getAnimeId, RankingAnimesFromQueryDto::getRank));

        List<RankingAnimesDto> animes;
        Long newLastId;
        AtomicReference<Long> displayRank = new AtomicReference<>(0L);

        if(genre != null) { // 장르가 있을 때는 순위를 재정렬해서 보여줌
            animes = allTimeRankingToday.stream()
                    .map(dto -> {
                        Long todayRank = dto.getRank();
                        Long yesterdayRank = yesterdayRankMap.get(dto.getAnimeId());
                        displayRank.getAndSet(displayRank.get() + 1);
                        List<AnimeGenresDto> genres = animeGenresMap.get(dto.getAnimeId());

                        return calcAndMakeAnimeRanking(todayRank, yesterdayRank, displayRank.get(), dto, genres, genre);
                    })
                    .toList();
            newLastId = animes.getLast().getRank();
        } else { // 장르가 없을 때는 DB 순위를 그대로 가져와서 보여줌
            animes = allTimeRankingToday.stream()
                    .map(dto -> {
                        Long todayRank = dto.getRank();
                        Long yesterdayRank = yesterdayRankMap.get(dto.getAnimeId());
                        displayRank.set(todayRank);
                        List<AnimeGenresDto> genres = animeGenresMap.get(dto.getAnimeId());

                        return calcAndMakeAnimeRanking(todayRank, yesterdayRank, displayRank.get(), dto, genres, genre);
                    })
                    .toList();
            newLastId = animes.getLast().getRank();
        }

        CursorDto cursor = CursorDto.of(newLastId);

        return RankingResponse.of(cursor, animes);
    }

    private RankingAnimesDto calcAndMakeAnimeRanking(Long todayRank, Long yesterdayRank, Long displayRank, RankingAnimesFromQueryDto dto, List<AnimeGenresDto> genres, String specificGenre) {
        long change;
        Trend trend;

        if(yesterdayRank == null) {
            change = 0;
            trend = Trend.NEW;
        } else {
            long diff = yesterdayRank - todayRank;
            change = Math.abs(diff);

            if(diff > 0) {
                trend = Trend.UP;
            } else if(diff < 0) {
                trend = Trend.DOWN;
            } else {
                trend = Trend.SAME;
            }
        }

        List<String> genreNames = genres.stream()
                .map(AnimeGenresDto::getGenreName)
                .collect(Collectors.toList());

        if(specificGenre != null && genreNames.contains(specificGenre)) {
            genreNames.sort((genre1, genre2) -> {
                if(genre1.equals(specificGenre)) {
                    return -1;
                }
                if(genre2.equals(specificGenre)) {
                    return 1;
                }

                return genre1.compareTo(genre2);
            });
        }

        return RankingAnimesDto.from(change, trend, displayRank, dto, genreNames);
    }
}
