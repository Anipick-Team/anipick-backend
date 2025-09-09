package com.anipick.backend.ranking.service;

import com.anipick.backend.anime.mapper.GenreMapper;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.ranking.domain.RankingDefaults;
import com.anipick.backend.ranking.domain.Trend;
import com.anipick.backend.ranking.dto.*;
import com.anipick.backend.ranking.mapper.RankingMapper;
import com.anipick.backend.ranking.mapper.RealTimeRankingMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankingService {
    private final RankingMapper rankingMapper;
    private final RealTimeRankingMapper realTimeRankingMapper;
    private final GenreMapper genreMapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    public RealTimeRankingResponse getRealTimeRanking(String genre, Long lastId, Long lastValue, Integer size) {
        try {
            String realTimeRankingKey;
            if(StringUtils.hasText(genre)) {
                Long genreId = genreMapper.findGenreIdByGenreName(genre);
                realTimeRankingKey = RankingDefaults.RANKING_ALIAS_KEY + genreId + RankingDefaults.COLON + RankingDefaults.CURRENT;
            } else {
                realTimeRankingKey = RankingDefaults.RANKING_GENRE_ALL_KEY + RankingDefaults.COLON + RankingDefaults.CURRENT;
            }

            String realTimeRankingJson = redisTemplate.opsForValue().get(realTimeRankingKey);

            RealTimeRankWrapper rankWrapper = objectMapper.readValue(realTimeRankingJson, RealTimeRankWrapper.class);
            List<RedisRealTimeRankingAnimesDto> redisAnimes = rankWrapper.getRealTimeRank().stream()
                    .map(RedisRealTimeRankingAnimesDto::of)
                    .toList();

            Map<Long, Long> rankMapByRedis = new HashMap<>();
            for(int i = 0; i < redisAnimes.size(); i++) {
                rankMapByRedis.put(redisAnimes.get(i).getAnimeId(), (long) (i + 1));
            }

            List<RealTimeRankingAnimesFromQueryDto> realTimeRanking = realTimeRankingMapper.getRealTimeRanking();
            List<RealTimeRankingAnimesFromQueryDto> realTimeRankingPaging = realTimeRankingMapper.getRealTimeRankingPaging(lastValue, lastId, size);
            List<Long> animeIds = realTimeRankingPaging.stream()
                    .map(RealTimeRankingAnimesFromQueryDto::getAnimeId)
                    .toList();
            List<AnimeGenresDto> genresByAnimeIds = rankingMapper.getGenresByAnimeIds(animeIds);

            Map<Long, List<AnimeGenresDto>> animeGenresMap = genresByAnimeIds.stream()
                    .collect(Collectors.groupingBy(AnimeGenresDto::getAnimeId));
            Map<Long, Long> rankMapByDb = new HashMap<>();
            for(int i = 0; i < realTimeRanking.size(); i++) {
                rankMapByDb.put(realTimeRanking.get(i).getAnimeId(), (long) (i + 1));
            }

            List<RealTimeRankingAnimesDto> animes = realTimeRankingPaging.stream()
                    .map(dto -> {
                        Long redisRank = rankMapByRedis.get(dto.getAnimeId());
                        Long dbRank = rankMapByDb.get(dto.getAnimeId());
                        String change;
                        String trend;
                        List<AnimeGenresDto> genres = Optional.ofNullable(animeGenresMap.get(dto.getAnimeId()))
                                .orElse(Collections.emptyList());
                        List<String> genreNames = getGenresByOrdering(genres, genre);

                        if(redisRank == null && dbRank != null) {
                            change = "N";
                            trend = changeToTrend(change);
                            return RealTimeRankingAnimesDto.from(dbRank, change, trend, dto, genreNames);
                        }

                        Long diff = redisRank - dbRank;
                        change = String.valueOf(diff);
                        trend = changeToTrend(change);

                        return RealTimeRankingAnimesDto.from(dbRank, change, trend, dto, genreNames);
                    })
                    .toList();

            Long newLastId;
            Long newLastValue;

            if(!doesRealTimeRankingExist(animes)) {
                CursorDto cursor = CursorDto.of(RankingDefaults.SORT, null, "null");

                return RealTimeRankingResponse.of(cursor, animes);
            } else {
                newLastId = animes.getLast().getPopularity();
                newLastValue = animes.getLast().getTrending();
                CursorDto cursor = CursorDto.of(RankingDefaults.SORT, newLastId, String.valueOf(newLastValue));

                return RealTimeRankingResponse.of(cursor, animes);
            }
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public RankingResponse getYearSeasonRanking(Integer year, Integer season, String genre, Long lastId, Long lastRank, Integer size) throws JsonProcessingException {
        List<RankingAnimesFromQueryDto> yearSeasonRankingPaging;
        Long genreId;

        if(StringUtils.hasText(genre)) {
            genreId = genreMapper.findGenreIdByGenreName(genre);
        } else {
            genreId = null;
        }

        yearSeasonRankingPaging = rankingMapper.getYearSeasonRankingPaging(year, season, genreId, lastId, size);

        List<Long> animeIds = yearSeasonRankingPaging.stream()
                .map(RankingAnimesFromQueryDto::getAnimeId)
                .toList();
        List<AnimeGenresDto> genresByAnimeIds = rankingMapper.getGenresByAnimeIds(animeIds);
        Map<Long, List<AnimeGenresDto>> animeGenresMap = genresByAnimeIds.stream()
                .collect(Collectors.groupingBy(AnimeGenresDto::getAnimeId));
        Long newLastId;
        AtomicReference<Long> displayRank = new AtomicReference<>(lastRank);

        List<RankingAnimesDto> animes = yearSeasonRankingPaging.stream()
                .map(dto -> {
                    displayRank.getAndSet(displayRank.get() + 1);
                    List<AnimeGenresDto> genres = Optional.ofNullable(animeGenresMap.get(dto.getAnimeId()))
                            .orElse(Collections.emptyList());
                    List<String> genreNames = getGenresByOrdering(genres, genre);
                    Long rank = displayRank.get();

                    return RankingAnimesDto.from(rank, dto, genreNames);
                })
                .toList();

        if(!doesRankingExist(animes)) {
            CursorDto cursor = CursorDto.of(null);

            return RankingResponse.of(cursor, animes);
        } else {
            newLastId = yearSeasonRankingPaging.getLast().getPopularity();
            CursorDto cursor = CursorDto.of(newLastId);

            return RankingResponse.of(cursor, animes);
        }
    }

    public RankingResponse getAllTimeRanking(String genre, Long lastId, Long lastRank, Integer size) {
        List<RankingAnimesFromQueryDto> allTimeRanking;
        Long genreId;

        if(StringUtils.hasText(genre)) {
            genreId = genreMapper.findGenreIdByGenreName(genre);
        } else {
            genreId = null;
        }

        allTimeRanking = rankingMapper.getAllTimeRankingPaging(genreId, lastId, size);

        List<Long> animeIds = allTimeRanking.stream()
                .map(RankingAnimesFromQueryDto::getAnimeId)
                .toList();
        List<AnimeGenresDto> genresByAnimeIds = rankingMapper.getGenresByAnimeIds(animeIds);
        Map<Long, List<AnimeGenresDto>> animeGenresMap = genresByAnimeIds.stream()
                .collect(Collectors.groupingBy(AnimeGenresDto::getAnimeId));
        Long newLastId;
        AtomicReference<Long> displayRank = new AtomicReference<>(lastRank);

        List<RankingAnimesDto> animes = allTimeRanking.stream()
                .map(dto -> {
                    displayRank.getAndSet(displayRank.get() + 1);
                    List<AnimeGenresDto> genres = Optional.ofNullable(animeGenresMap.get(dto.getAnimeId()))
                            .orElse(Collections.emptyList());
                    List<String> genreNames = getGenresByOrdering(genres, genre);
                    Long rank = displayRank.get();

                    return RankingAnimesDto.from(rank, dto, genreNames);
                })
                .toList();

        if(!doesRankingExist(animes)) {
            CursorDto cursor = CursorDto.of(null);

            return RankingResponse.of(cursor, animes);
        } else {
            newLastId = allTimeRanking.getLast().getPopularity();
            CursorDto cursor = CursorDto.of(newLastId);

            return RankingResponse.of(cursor, animes);
        }
    }

    private String changeToTrend(String change) {
        Trend trend;

        if(change.equals("N")) {
            trend = Trend.NEW;
        } else {
            long changeAsLong = Long.parseLong(change);

            if(changeAsLong > 0) {
                trend = Trend.UP;
            } else if(changeAsLong < 0) {
                trend = Trend.DOWN;
            } else {
                trend = Trend.SAME;
            }
        }

        return trend.toString().toLowerCase();
    }

    private List<String> getGenresByOrdering(List<AnimeGenresDto> genres, String specificGenre) {
        if(genres == null || genres.isEmpty()) {
            return Collections.emptyList();
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

        return genreNames;
    }

    private boolean doesRankingExist(List<RankingAnimesDto> animes) {
        return animes != null && !animes.isEmpty();
    }

    private boolean doesRealTimeRankingExist(List<RealTimeRankingAnimesDto> animes) {
        return animes != null && !animes.isEmpty();
    }
}
