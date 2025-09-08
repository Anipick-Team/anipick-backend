package com.anipick.backend.home.service;

import com.anipick.backend.anime.dto.ComingSoonItemBasicDto;
import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.home.dto.HomeComingSoonItemDto;
import com.anipick.backend.home.dto.HomeRecentReviewItemDto;
import com.anipick.backend.home.dto.HomeTrendingRankingDto;
import com.anipick.backend.home.dto.TrendingRankingFromQueryDto;
import com.anipick.backend.home.mapper.HomeMapper;
import com.anipick.backend.ranking.domain.RankingDefaults;
import com.anipick.backend.ranking.dto.RedisRealTimeRankingAnimesDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeMapper homeMapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    private static final DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd");
    private static final int LIMIT_SIZE = 10;

    @Value("${anime.default-cover-url}")
	private String defaultCoverUrl;

    public List<HomeTrendingRankingDto> getTrendingRanking() {
        try {
            String redisRankingKey = RankingDefaults.RANKING_GENRE_ALL_KEY + RankingDefaults.COLON + RankingDefaults.CURRENT;
            String redisRankingJson = redisTemplate.opsForValue().get(redisRankingKey);
            List<RedisRealTimeRankingAnimesDto> redisAnimes = objectMapper.readValue(redisRankingJson, new TypeReference<List<RedisRealTimeRankingAnimesDto>>() {});
            List<RedisRealTimeRankingAnimesDto> redisHomeRanking = redisAnimes.stream()
                    .limit(LIMIT_SIZE)
                    .toList();
            List<Long> animeIds = redisHomeRanking.stream()
                    .map(RedisRealTimeRankingAnimesDto::getAnimeId)
                    .toList();
            List<TrendingRankingFromQueryDto> trendingRanking = homeMapper.selectHomeTrendingRanking(animeIds);
            AtomicReference<Long> displayRank = new AtomicReference<>(0L);

            List<HomeTrendingRankingDto> homeTrendingRanking = trendingRanking.stream()
                    .map(dto -> {
                        displayRank.set(displayRank.get() + 1);
                        Long rank = displayRank.get();

                        return HomeTrendingRankingDto.of(dto, rank);
                    })
                    .toList();

            return homeTrendingRanking;
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<HomeRecentReviewItemDto> getRecentReviews(Long userId) {
        List<HomeRecentReviewItemDto> raws = homeMapper.selectHomeRecentReviews(userId, 10);

        List<HomeRecentReviewItemDto> items = raws.stream()
                .map(dto -> {
                    LocalDateTime dateTime = LocalDateTime.parse(dto.getCreatedAt(), parser);
                    String formattedDate = dateTime.format(formatter);

                    return HomeRecentReviewItemDto.of(
                            dto.getReviewId(),
                            dto.getAnimeId(),
                            dto.getAnimeTitle(),
                            dto.getReviewContent(),
                            dto.getNickname(),
                            formattedDate
                    );
                })
                .toList();
        return items;
    }

    @Transactional(readOnly = true)
    public List<HomeComingSoonItemDto> getComingSoonAnimes() {
        SortOption sortOption = SortOption.LATEST;
        String orderByQuery = sortOption.getOrderByQuery();
        List<ComingSoonItemBasicDto> comingSoonItemBasicDtos = homeMapper.selectHomeComingSoonAnimes(defaultCoverUrl, orderByQuery, 10);

        List<ComingSoonItemBasicDto> typeToReleaseDateList = comingSoonItemBasicDtos.stream()
                .map(ComingSoonItemBasicDto::typeToReleaseDate)
                .toList();

        List<HomeComingSoonItemDto> items = typeToReleaseDateList.stream()
                .map(dto -> HomeComingSoonItemDto.of(
                        dto.getAnimeId(),
                        dto.getTitle(),
                        dto.getCoverImageUrl(),
                        dto.getStartDate()
                ))
                .toList();
        return items;
    }
}
