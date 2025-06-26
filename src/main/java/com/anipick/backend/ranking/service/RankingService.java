package com.anipick.backend.ranking.service;

import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.ranking.domain.RankingDefaults;
import com.anipick.backend.ranking.domain.Trend;
import com.anipick.backend.ranking.dto.RankingAnimesDto;
import com.anipick.backend.ranking.dto.RankingAnimesFromQueryDto;
import com.anipick.backend.ranking.dto.RankingResponse;
import com.anipick.backend.ranking.mapper.RankingMapper;
import com.anipick.backend.ranking.mapper.RealTimeRankingMapper;
import com.anipick.backend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingMapper rankingMapper;
    private final RealTimeRankingMapper realTimeRankingMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

//    public RankingResponse getRealTimeRanking(String genre, Long lastId, Integer size) {
//        LocalDateTime now = LocalDateTime.now();
//
//        redisTemplate.opsForValue().get(RankingDefaults.RANKING_SNAPSHOT_KEY.formatted());
//    }

    public RankingResponse getYearSeasonRanking(Integer year, Integer season, String genre, Long lastId, Integer size) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(RankingDefaults.ONE_DAY);

        List<RankingAnimesFromQueryDto> yearSeasonRankingToday = rankingMapper.getYearSeasonRankingPaging(year, season, genre, lastId, size, today);
        List<RankingAnimesFromQueryDto> yearSeasonRankingYesterday = rankingMapper.getYearSeasonRanking(year, season, genre, yesterday);

        Map<Long, Long> yesterdayRankMap =  yearSeasonRankingYesterday.stream()
                .collect(Collectors.toMap(
                        RankingAnimesFromQueryDto::getAnimeId,
                        RankingAnimesFromQueryDto::getRank
                ));

        List<RankingAnimesDto> animes;
        AtomicReference<Long> displayRank = new AtomicReference<>(0L);

        if(genre != null) {
            animes = yearSeasonRankingToday.stream()
                    .map(dto -> {
                        Long todayRank = dto.getRank();
                        Long yesterdayRank = yesterdayRankMap.get(dto.getAnimeId());
                        displayRank.getAndSet(displayRank.get() + 1);

                        return calcAndMakeAnimeRanking(todayRank, yesterdayRank, displayRank.get(), dto);
                    })
                    .toList();
        } else {
            animes = yearSeasonRankingToday.stream()
                    .map(dto -> {
                        Long todayRank = dto.getRank();
                        Long yesterdayRank = yesterdayRankMap.get(dto.getAnimeId());
                        displayRank.set(todayRank);

                        return calcAndMakeAnimeRanking(todayRank, yesterdayRank, displayRank.get(), dto);
                    })
                    .toList();
        }

        CursorDto cursor = CursorDto.of(lastId);

        return RankingResponse.of(cursor, animes);
    }

    public RankingResponse getAllTimeRanking(String genre, Long lastId, Integer size) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(RankingDefaults.ONE_DAY);

        List<RankingAnimesFromQueryDto> allTimeRankingToday = rankingMapper.getAllTimeRankingPaging(genre, lastId, size, today);
        List<RankingAnimesFromQueryDto> allTimeRankingYesterday = rankingMapper.getAllTimeRanking(genre, yesterday);

        Map<Long, Long> yesterdayRankMap = allTimeRankingYesterday.stream()
                .collect(Collectors.toMap(RankingAnimesFromQueryDto::getAnimeId, RankingAnimesFromQueryDto::getRank));

        List<RankingAnimesDto> animes;
        AtomicReference<Long> displayRank = new AtomicReference<>(0L);

        if(genre != null) { // 장르가 있을 때는 순위를 재정렬해서 보여줌
            animes = allTimeRankingToday.stream()
                    .map(dto -> {
                        Long todayRank = dto.getRank();
                        Long yesterdayRank = yesterdayRankMap.get(dto.getAnimeId());
                        displayRank.getAndSet(displayRank.get() + 1);

                        return calcAndMakeAnimeRanking(todayRank, yesterdayRank, displayRank.get(), dto);
                    })
                    .toList();
        } else { // 장르가 없을 때는 DB 순위를 그대로 가져와서 보여줌
            animes = allTimeRankingToday.stream()
                    .map(dto -> {
                        Long todayRank = dto.getRank();
                        Long yesterdayRank = yesterdayRankMap.get(dto.getAnimeId());
                        displayRank.set(todayRank);

                        return calcAndMakeAnimeRanking(todayRank, yesterdayRank, displayRank.get(), dto);
                    })
                    .toList();
        }

        CursorDto cursor = CursorDto.of(lastId);

        return RankingResponse.of(cursor, animes);
    }

    private RankingAnimesDto calcAndMakeAnimeRanking(Long todayRank, Long yesterdayRank, Long displayRank, RankingAnimesFromQueryDto dto) {
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

        return RankingAnimesDto.from(change, trend, displayRank, dto);
    }
}
