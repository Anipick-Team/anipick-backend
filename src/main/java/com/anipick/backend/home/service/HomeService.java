package com.anipick.backend.home.service;

import com.anipick.backend.anime.domain.Anime;
import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.anime.dto.ComingSoonItemAllTitleDto;
import com.anipick.backend.anime.dto.ComingSoonItemBasicDto;
import com.anipick.backend.anime.mapper.AnimeMapper;
import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.home.dto.HomeComingSoonItemDto;
import com.anipick.backend.home.dto.HomeRecentReviewItemDto;
import com.anipick.backend.home.dto.HomeTrendingRankingDto;
import com.anipick.backend.home.dto.TrendingRankingFromQueryDto;
import com.anipick.backend.home.dto.HomeRecommendationItemDto;
import com.anipick.backend.home.mapper.HomeMapper;
import com.anipick.backend.recommendation.domain.UserRecommendMode;
import com.anipick.backend.recommendation.domain.UserRecommendState;
import com.anipick.backend.recommendation.dto.*;
import com.anipick.backend.recommendation.mapper.AnimeTagMapper;
import com.anipick.backend.recommendation.mapper.RecommendMapper;
import com.anipick.backend.recommendation.mapper.RecommendReviewUserMapper;
import com.anipick.backend.recommendation.mapper.UserRecommendStateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeMapper homeMapper;
    private final UserRecommendStateMapper userRecommendStateMapper;
    private final RecommendReviewUserMapper reviewUserMapper;
    private final AnimeTagMapper animeTagMapper;
    private final RecommendMapper recommendMapper;
    private final AnimeMapper animeMapper;

    private static final DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd");
    private static final int LIMIT_SIZE = 10;

    @Value("${anime.default-cover-url}")
    private String defaultCoverUrl;

    public List<HomeTrendingRankingDto> getTrendingRanking() {
        List<TrendingRankingFromQueryDto> trendingRanking = homeMapper.selectHomeTrendingRanking(LIMIT_SIZE);
        AtomicReference<Long> displayRank = new AtomicReference<>(0L);

        List<HomeTrendingRankingDto> homeTrendingRanking = trendingRanking.stream()
                .map(dto -> {
                    displayRank.set(displayRank.get() + 1);
                    Long rank = displayRank.get();

                    return HomeTrendingRankingDto.of(dto, rank);
                })
                .toList();

        return homeTrendingRanking;
    }

    @Transactional(readOnly = true)
    public List<HomeRecentReviewItemDto> getRecentReviews(Long userId) {
        List<HomeRecentReviewItemDto> raws = homeMapper.selectHomeRecentReviews(userId, 10)
                .stream()
                .map(HomeRecentReviewItemDto::animeTitleTranslationPick)
                .toList();

        List<HomeRecentReviewItemDto> items = raws.stream()
                .map(dto -> {
                    LocalDateTime dateTime = LocalDateTime.parse(dto.getCreatedAt(), parser);
                    String formattedDate = dateTime.format(formatter);

                    return HomeRecentReviewItemDto.of(
                            dto.getReviewId(),
                            dto.getUserId(),
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
        List<ComingSoonItemBasicDto> comingSoonItemBasicDtos = homeMapper.selectHomeComingSoonAnimes(defaultCoverUrl, orderByQuery, 10)
                .stream()
                .map(ComingSoonItemBasicDto::animeTitleTranslationPick)
                .toList();

        List<HomeComingSoonItemDto> items = comingSoonItemBasicDtos.stream()
                .map(dto -> HomeComingSoonItemDto.of(
                        dto.getAnimeId(),
                        dto.getTitle(),
                        dto.getCoverImageUrl(),
                        dto.getStartDate()
                ))
                .toList();
        return items;
    }

    public HomeRecommendationItemDto getRecommendations(Long userId) {
        UserRecommendState userState = userRecommendStateMapper.findByUserId(userId);

        if (userState == null) {
            Long referenceAnimeId = reviewUserMapper.findMostRecentHighRateAnime(userId);
            userRecommendStateMapper.insertInitialState(userId, UserRecommendMode.RECENT_HIGH, referenceAnimeId);
            userState = UserRecommendState.createRecommendState(userId, referenceAnimeId);
        }

        if (userState.getMode() == UserRecommendMode.RECENT_HIGH) {
            long days = Duration.between(userState.getStartDate(), LocalDateTime.now()).toDays();
            if (days >= 3) {
                userRecommendStateMapper.updateMode(userId, UserRecommendMode.TAG_BASED, null);
                userState = userRecommendStateMapper.findByUserId(userId);
            } else {
                Long latestHigh = reviewUserMapper.findMostRecentHighRateAnime(userId);
                // 리뷰가 없을 경우
                if (latestHigh == null) {
                    return HomeRecommendationItemDto.of(null, List.of());
                }
                if (!latestHigh.equals(userState.getReferenceAnimeId())) {
                    userRecommendStateMapper.updateReferenceAnime(userId, latestHigh);
                    userState.setReferenceAnimeId(latestHigh);
                }
            }
        }

        List<AnimeItemDto> resultAnimes;

        if (userState.getMode() == UserRecommendMode.RECENT_HIGH) {
            Long referenceAnimeId = reviewUserMapper.findMostRecentHighRateAnime(userId);
            List<Long> tagIds = animeTagMapper.findTopTagsByAnime(referenceAnimeId, 5);

            RecentHighCountOnlyRequest request =
                    RecentHighCountOnlyRequest.of(userId, referenceAnimeId, tagIds, null, null, 10L);

            List<AnimeItemRecommendTagCountDto> recommendAnimes = recommendMapper.selectUserRecentHighAnimes(request)
                    .stream()
                    .map(AnimeItemRecommendTagCountDto::animeTitleTranslationPick)
                    .toList();

            resultAnimes = recommendAnimes.stream()
                    .map(rec -> new AnimeItemDto(
                            rec.getAnimeId(),
                            rec.getTitle(),
                            rec.getCoverImage()
                    ))
                    .toList();
        } else {
            List<Long> topRatedAnimeIds = reviewUserMapper.findTopRatedAnimeIds(userId, 20);

            List<Long> filteredIds = topRatedAnimeIds.stream()
                    .filter(topRatedAnimeIds::contains)
                    .toList();

            List<TagScoreDto> tagScores = recommendMapper.selectTagScoresForUser(userId, filteredIds);

            List<Long> tagIds = tagScores.stream()
                    .sorted(Comparator.comparingDouble(TagScoreDto::getScore).reversed())
                    .limit(10)
                    .map(TagScoreDto::getTagId)
                    .toList();

            TagBasedCountOnlyRequest request =
                    TagBasedCountOnlyRequest.of(userId, tagIds, null, null, 10L);

            List<AnimeItemRecommendTagCountDto> recommendAnimes = recommendMapper.selectTagBasedAnimes(request)
                    .stream()
                    .map(AnimeItemRecommendTagCountDto::animeTitleTranslationPick)
                    .toList();

            resultAnimes = recommendAnimes.stream()
                    .map(rec -> new AnimeItemDto(
                            rec.getAnimeId(),
                            rec.getTitle(),
                            rec.getCoverImage()
                    ))
                    .toList();
        }
        return HomeRecommendationItemDto.of(null, resultAnimes);
    }

    public HomeRecommendationItemDto getLastDetailAnimeRecommendations(Long userId, Long animeId) {
        Anime anime = animeMapper.selectAnimeByAnimeId(animeId);
        String referenceAnimeTitle = anime.getTitleKor();

        List<AnimeItemDto> resultAnimes;

        List<Long> tagIds = animeTagMapper.findTopTagsByAnime(animeId, 5);

        RecentHighCountOnlyRequest request =
                RecentHighCountOnlyRequest.of(userId, animeId, tagIds, null, null, 10L);

        List<AnimeItemRecommendTagCountDto> recommendAnimes = recommendMapper.selectUserRecentHighAnimes(request)
                .stream()
                .map(AnimeItemRecommendTagCountDto::animeTitleTranslationPick)
                .toList();

        resultAnimes = recommendAnimes.stream()
                .map(rec -> new AnimeItemDto(
                        rec.getAnimeId(),
                        rec.getTitle(),
                        rec.getCoverImage()
                ))
                .toList();

        return HomeRecommendationItemDto.of(referenceAnimeTitle, resultAnimes);
    }
}
