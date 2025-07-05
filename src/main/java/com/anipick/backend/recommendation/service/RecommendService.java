package com.anipick.backend.recommendation.service;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.recommendation.domain.UserRecommendMode;
import com.anipick.backend.recommendation.domain.UserRecommendState;
import com.anipick.backend.recommendation.dto.*;
import com.anipick.backend.recommendation.mapper.AnimeTagMapper;
import com.anipick.backend.recommendation.mapper.RecommendReviewUserMapper;
import com.anipick.backend.recommendation.mapper.RecommendMapper;
import com.anipick.backend.recommendation.mapper.UserRecommendStateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {

    private final UserRecommendStateMapper userRecommendStateMapper;
    private final RecommendReviewUserMapper reviewUserMapper;
    private final AnimeTagMapper animeTagMapper;
    private final RecommendMapper recommendMapper;

    public UserMainRecommendationPageDto getRecommendations(Long userId, Long lastId, Long lastValue, Long size) {
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
                    return UserMainRecommendationPageDto.of(CursorDto.of(null), List.of());
                }
                if (!latestHigh.equals(userState.getReferenceAnimeId())) {
                    userRecommendStateMapper.updateReferenceAnime(userId, latestHigh);
                    userState.setReferenceAnimeId(latestHigh);
                }
            }
        }

        List<AnimeItemDto> resultAnimes;
        List<AnimeItemRecommendTagCountDto> recommendTagCountDtoAnimes;

        if (userState.getMode() == UserRecommendMode.RECENT_HIGH) {
            Long referenceAnimeId = reviewUserMapper.findMostRecentHighRateAnime(userId);
            List<Long> tagIds = animeTagMapper.findTopTagsByAnime(referenceAnimeId, 5);

            RecentHighCountOnlyRequest request =
                    RecentHighCountOnlyRequest.of(userId, referenceAnimeId, tagIds, lastValue, lastId, size);

            List<AnimeItemRecommendTagCountDto> recommendAnimes = recommendMapper.selectUserRecentHighAnimes(request);
            recommendTagCountDtoAnimes = recommendAnimes;

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
                    TagBasedCountOnlyRequest.of(userId, tagIds, lastValue, lastId, size);

            List<AnimeItemRecommendTagCountDto> recommendAnimes = recommendMapper.selectTagBasedAnimes(request);
            recommendTagCountDtoAnimes = recommendAnimes;

            resultAnimes = recommendAnimes.stream()
                    .map(rec -> new AnimeItemDto(
                            rec.getAnimeId(),
                            rec.getTitle(),
                            rec.getCoverImage()
                    ))
                    .toList();
        }

        CursorDto cursor;

        if (resultAnimes.isEmpty()) {
            cursor = CursorDto.of(null, null, null);
        } else {
            Long nextId = recommendTagCountDtoAnimes.getLast().getAnimeId();
            Long nextValue = recommendTagCountDtoAnimes.getLast().getTagCount();
            cursor = CursorDto.of(null, nextId, nextValue.toString());
        }

        return UserMainRecommendationPageDto.of(cursor, resultAnimes);
    }
}
