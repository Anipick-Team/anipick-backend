package com.anipick.backend.ranking.service;

import com.anipick.backend.ranking.component.RankingSnapshotScheduler;
import com.anipick.backend.ranking.domain.RankingDefaults;
import com.anipick.backend.ranking.dto.RankingAnimesFromQueryDto;
import com.anipick.backend.ranking.dto.RankingResponse;
import com.anipick.backend.ranking.mapper.RankingMapper;
import com.anipick.backend.ranking.mapper.RealTimeRankingMapper;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingMapper rankingMapper;
    private final RealTimeRankingMapper realTimeRankingMapper;
    private final UserMapper userMapper;
    private final RankingSnapshotScheduler scheduler;
    private final RedisTemplate<String, Object> redisTemplate;

    public RankingResponse getRealTimeRanking(String genre, Long lastId, Integer size, Long userId) {

    }

    public RankingResponse getYearSeasonRanking(Integer year, Integer season, String genre, Long lastId, Integer size) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(RankingDefaults.ONE_DAY);

        List<RankingAnimesFromQueryDto> yearSeasonRankingToday = rankingMapper.getYearSeasonRanking(year, season, genre, lastId, size, today);
        List<RankingAnimesFromQueryDto> yearSeasonRankingYesterday = rankingMapper.getYearSeasonRanking(year, season, genre, lastId, size, yesterday);



    }

    public RankingResponse getAllTimeRanking(String genre, Long lastId, Integer size, Long userId) {

    }
}
