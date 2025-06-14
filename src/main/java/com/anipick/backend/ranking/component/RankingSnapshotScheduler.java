package com.anipick.backend.ranking.component;

import com.anipick.backend.anime.domain.Season;
import com.anipick.backend.anime.dto.GenreDto;
import com.anipick.backend.anime.mapper.MetaDataMapper;
import com.anipick.backend.ranking.domain.RankingDefaults;
import com.anipick.backend.ranking.dto.RankingAnimesFromQueryDto;
import com.anipick.backend.ranking.mapper.RankingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RankingSnapshotScheduler {
    private final RankingMapper rankingMapper;
    private final MetaDataMapper metaDataMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled(cron = "0 0 0 * * *")
    public void saveTodaySeasonYearRankingSnapshot() {
        LocalDate today = LocalDate.now();
        String snapshotKey = RankingDefaults.RANKING_SNAPSHOT_KEY + today.format(DateTimeFormatter.BASIC_ISO_DATE);
        List<String> genres = metaDataMapper.selectAllGenres().stream()
                .map(GenreDto::getName)
                .toList();

        for(int year = RankingDefaults.MIN_YEAR; year <= RankingDefaults.MAX_YEAR; year++) {
            for(int season = Season.Q1.getCode(); season <= Season.Q4.getCode(); season++) {
                saveRankingFromNotFilterSnapshot(year, season, today);

                for(String genre : genres) {
                    saveRankingFromFilterSnapshot(year, season, genre, today);
                }
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void saveTodayAllTimeRankingSnapshot() {

    }

    private void saveRankingFromNotFilterSnapshot(int year, int season, LocalDate snapshotDate) {
        List<RankingAnimesFromQueryDto> yearSeasonRankingFromNotFilter = rankingMapper.getYearSeasonRankingFromNotFilter(year, season, snapshotDate);
        if(yearSeasonRankingFromNotFilter.isEmpty()) {
            return;
        }

        Map<Long, Long> rankingMap = yearSeasonRankingFromNotFilter.stream()
                .collect(Collectors.toMap(
                        RankingAnimesFromQueryDto::getAnimeId,
                        RankingAnimesFromQueryDto::getRank
                ));

        redisTemplate.opsForValue().set(RankingDefaults.RANKING_SNAPSHOT_KEY.formatted(year, season, snapshotDate), rankingMap);
    }

    private void saveRankingFromFilterSnapshot(int year, int season, String genre, LocalDate snapshotDate) {
        List<RankingAnimesFromQueryDto> yearSeasonRankingFromFilter = rankingMapper.getYearSeasonRankingFromFilter(year, season, genre, snapshotDate);
        if(yearSeasonRankingFromFilter.isEmpty()) {
            return;
        }

        Map<Long, Long> rankingMap = yearSeasonRankingFromFilter.stream()
                .collect(Collectors.toMap(
                        RankingAnimesFromQueryDto::getAnimeId,
                        RankingAnimesFromQueryDto::getRank
                ));

        redisTemplate.opsForValue().set(RankingDefaults.RANKING_SNAPSHOT_KEY.formatted(year, season, snapshotDate), rankingMap);
    }
}
