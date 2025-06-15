package com.anipick.backend.ranking.component;

import com.anipick.backend.anime.domain.Season;
import com.anipick.backend.anime.dto.GenreDto;
import com.anipick.backend.anime.mapper.MetaDataMapper;
import com.anipick.backend.ranking.domain.RankingDefaults;
import com.anipick.backend.ranking.dto.RankingAnimesFromQueryDto;
import com.anipick.backend.ranking.mapper.RankingMapper;
import com.anipick.backend.ranking.mapper.RealTimeRankingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RankingSnapshotScheduler {
    private final RealTimeRankingMapper realTimeRankingMapper;
    private final MetaDataMapper metaDataMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Scheduled(cron = "0 0 * * * *")
    public void saveRealTimeRankingSnapshot() {
        LocalDateTime now = LocalDateTime.now();
        List<String> genres = metaDataMapper.selectAllGenres().stream()
                .map(GenreDto::getName)
                .toList();
        String currentTime = now.format(formatter);

        for(String genre : genres) {
            saveRankingSnapshot(genre, currentTime);
        }

        saveRankingSnapshot(null, currentTime);
    }

    private void saveRankingSnapshot(String genre, String snapshotDateTime) {
        List<RankingAnimesFromQueryDto> realTimeRanking = realTimeRankingMapper.getRealTimeRanking(genre, snapshotDateTime);
        if(realTimeRanking.isEmpty()) {
            return;
        }

        Map<Long, Long> rankingMap = realTimeRanking.stream()
                .collect(Collectors.toMap(
                        RankingAnimesFromQueryDto::getAnimeId,
                        RankingAnimesFromQueryDto::getRank
                ));

        redisTemplate.opsForValue().set(RankingDefaults.RANKING_SNAPSHOT_KEY.formatted(snapshotDateTime), rankingMap);
    }
}
