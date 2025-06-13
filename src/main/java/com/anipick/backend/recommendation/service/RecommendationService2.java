package com.anipick.backend.recommendation.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.anipick.backend.recommendation.dto.*;
import com.anipick.backend.test.TestAnimeItemDto;
import com.anipick.backend.test.TestAnimeTagDto;
import com.anipick.backend.test.TestMapper;
import org.springframework.stereotype.Service;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.recommendation.domain.UserRecommendMode;
import com.anipick.backend.recommendation.domain.UserState;
import com.anipick.backend.recommendation.mapper.AnimeTagMapper;
import com.anipick.backend.recommendation.mapper.RecommendationMapper;
import com.anipick.backend.recommendation.mapper.ReviewUserMapper;
import com.anipick.backend.recommendation.mapper.UserStateMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService2 {
    private final UserStateMapper userStateMapper;
    private final ReviewUserMapper reviewUserMapper;
    private final RecommendationMapper recommendationMapper;
    private final AnimeTagMapper animeTagMapper;
    private final TestMapper testMapper;

    public UserMainRecommendationPageDto test1(Long lastValue, Long lastCount, Long lastId, Long size, Long userId, UserRecommendMode mode, int topN, double topPercent) {
        UserState state = userStateMapper.findByUserId(userId);
        if (state == null) {
            Long referenceAnimeId = reviewUserMapper.findMostRecentHighRatedAnime(userId);
            userStateMapper.insertInitialState(userId, UserRecommendMode.RECENT_HIGH, referenceAnimeId);
            state = new UserState(userId, UserRecommendMode.RECENT_HIGH, referenceAnimeId, LocalDateTime.now());
        }

        // RECENT_HIGH / TAG_BASED 전환 체크
        if (state.getMode() == UserRecommendMode.RECENT_HIGH) {
            long days = Duration.between(state.getStartDate(), LocalDateTime.now()).toDays();
            if (days >= 3) {
                userStateMapper.updateMode(userId, UserRecommendMode.TAG_BASED, null);
                state = userStateMapper.findByUserId(userId);
            } else {
                Long latestHigh = reviewUserMapper.findMostRecentHighRatedAnime(userId);
                if (latestHigh == null) {
                    // 리뷰가 없음
                    return UserMainRecommendationPageDto.of(0L, CursorDto.of((Long) null), List.of(), List.of());
                }
                if (!latestHigh.equals(state.getReferenceAnimeId())) {
                    userStateMapper.updateReferenceAnime(userId, latestHigh);
                    state.setReferenceAnimeId(latestHigh);
                }
            }
        }
        long totalCount = 0;
        List<AnimeRecommendDto2> animes;
        List<AnimeRecommendDto2> countOnlyList;
        List<TestAnimeItemDto> referenceAnimes = new ArrayList<>();
        List<TestAnimeItemDto> resultAnimes  = new ArrayList<>();
        if (mode == UserRecommendMode.RECENT_HIGH) {
//			 Count-Only 버전 (tag_count 만 반환)
//            Long referenceAnimeId = state.getReferenceAnimeId();
            Long referenceAnimeId = reviewUserMapper.findMostRecentHighRatedAnime(userId);
            List<Long> tagIds = animeTagMapper.findTopTagsByAnime(referenceAnimeId, 5);
            RecentHighCountOnlyRequest countOnlyRequest =
                    new RecentHighCountOnlyRequest(userId, referenceAnimeId, tagIds, lastCount, lastId, size);
            List<AnimeItemDto> refAnimes = testMapper.selectReferenceAnime(referenceAnimeId);
            List<TestAnimeTagDto> tags = testMapper.getAnimeTags(referenceAnimeId);

            referenceAnimes = refAnimes.stream()
					.map(ref -> new TestAnimeItemDto(
							ref.getAnimeId(),
							ref.getTitle(),
							ref.getCoverImageUrl(),
							tags
					))
					.toList();

            List<AnimeRecommendDto2> countOnlyList1 = recommendationMapper.selectRecentHighCountOnly(countOnlyRequest);

            resultAnimes = countOnlyList1.stream()
                    .map(rec -> new TestAnimeItemDto(
                            rec.getAnimeId(),
                            rec.getTitle(),
                            rec.getCoverImageUrl(),
                            testMapper.getAnimeTags(rec.getAnimeId())
                    ))
                    .toList();
            animes = countOnlyList1;
            totalCount = recommendationMapper.countRecentHighCountOnly(countOnlyRequest);

        } else {
            // tag based
            // 유저 리뷰 전부 불러와서 상위 20% & 최대 20개 애니 추출
            List<Long> topRatedAnimeIds = reviewUserMapper.findTopRatedAnimeIds(userId, topN);

			long totalReviews = reviewUserMapper.countReviewsByUser(userId);
			int percentLimit = (int) Math.ceil(totalReviews * (topPercent / 100.0));

            List<Long> topPercentIds = reviewUserMapper.findTopRatedAnimeIds(userId, percentLimit);

			List<Long> filteredIds = topRatedAnimeIds.stream()
					.filter(topPercentIds::contains)
					.toList();

            referenceAnimes = filteredIds.stream()
   					 .map(a -> {
							AnimeItemDto info = testMapper.selectReferenceAnime(a).get(0);
							List<TestAnimeTagDto> tags = testMapper.getAnimeTags(a);
							return new TestAnimeItemDto(
									info.getAnimeId(), info.getTitle(), info.getCoverImageUrl(), tags
							);
						})
					.toList();

            // 이 애니들에 등장한 태그별 (rating * weight) 합산 → 상위 10개 tagIds 추출
            List<TagScoreDto> tagScores = recommendationMapper.selectTagScoresForUser(
                    userId, filteredIds
            );
            List<Long> tagIds = tagScores.stream()
                    .sorted(Comparator.comparingDouble(TagScoreDto::getScore).reversed())
                    .limit(10)
                    .map(TagScoreDto::getTagId)
                    .toList();

//			 Count-Only 버전
            TagBasedCountOnlyRequest cntReq = new TagBasedCountOnlyRequest(
                    userId, tagIds,
                    lastCount, lastId, size
            );
            List<AnimeRecommendDto2> countOnlyList1 = recommendationMapper.selectTagBasedCountOnly(cntReq);
            resultAnimes = countOnlyList1.stream()
                    .map(rec -> new TestAnimeItemDto(
                            rec.getAnimeId(),
                            rec.getTitle(),
                            rec.getCoverImageUrl(),
                            testMapper.getAnimeTags(rec.getAnimeId())
                    ))
                    .toList();
            animes = countOnlyList1;
            totalCount = recommendationMapper.countTagBasedCountOnly(cntReq);
        }

        CursorDto cursor;

        if (animes.isEmpty()) {
            cursor = CursorDto.of((String) null, null, null);
        } else {
            Long nextId = animes.getLast().getAnimeId();
            Long nextCount = animes.getLast().getTagCount();
            cursor = CursorDto.of(null, nextId, String.valueOf(nextCount));
        }

        List<AnimeItemDto> items = animes
                .stream()
                .map(e -> new AnimeItemDto(e.getAnimeId(), e.getTitle(), e.getCoverImageUrl()))
                .toList();

        return UserMainRecommendationPageDto.of(totalCount, cursor, referenceAnimes, resultAnimes);
    }

    public UserMainRecommendationPageDto test2(Long lastValue, Long lastCount, Long lastId, Long size, Long userId, UserRecommendMode mode, int topN, double topPercent) {
        UserState state = userStateMapper.findByUserId(userId);
        if (state == null) {
            Long referenceAnimeId = reviewUserMapper.findMostRecentHighRatedAnime(userId);
            userStateMapper.insertInitialState(userId, UserRecommendMode.RECENT_HIGH, referenceAnimeId);
            state = new UserState(userId, UserRecommendMode.RECENT_HIGH, referenceAnimeId, LocalDateTime.now());
        }

        // RECENT_HIGH / TAG_BASED 전환 체크
        if (state.getMode() == UserRecommendMode.RECENT_HIGH) {
            long days = Duration.between(state.getStartDate(), LocalDateTime.now()).toDays();
            if (days >= 3) {
                userStateMapper.updateMode(userId, UserRecommendMode.TAG_BASED, null);
                state = userStateMapper.findByUserId(userId);
            } else {
                Long latestHigh = reviewUserMapper.findMostRecentHighRatedAnime(userId);
                if (latestHigh == null) {
                    // 리뷰가 없음
                    return UserMainRecommendationPageDto.of(0L, CursorDto.of((Long) null), List.of(), null);
                }
                if (!latestHigh.equals(state.getReferenceAnimeId())) {
                    userStateMapper.updateReferenceAnime(userId, latestHigh);
                    state.setReferenceAnimeId(latestHigh);
                }
            }
        }
        long totalCount = 0;
        List<AnimeRecommendDto2> animes;
        List<AnimeRecommendDto2> scoreOnlyList;
        List<TestAnimeItemDto> referenceAnimes = new ArrayList<>();
        List<TestAnimeItemDto> resultAnimes  = new ArrayList<>();

        if (mode == UserRecommendMode.RECENT_HIGH) {
//            Long referenceAnimeId = state.getReferenceAnimeId();
            Long referenceAnimeId = reviewUserMapper.findMostRecentHighRatedAnime(userId);

            List<Long> tagIds = animeTagMapper.findTopTagsByAnime(referenceAnimeId, 5);
            RecentHighScoreOnlyRequest scoreOnlyRequest =
                    new RecentHighScoreOnlyRequest(userId, referenceAnimeId, tagIds, lastCount, lastId, size);

            List<AnimeItemDto> refAnimes = testMapper.selectReferenceAnime(referenceAnimeId);
            List<TestAnimeTagDto> tags = testMapper.getAnimeTags(referenceAnimeId);

            referenceAnimes = refAnimes.stream()
					.map(ref -> new TestAnimeItemDto(
							ref.getAnimeId(),
							ref.getTitle(),
							ref.getCoverImageUrl(),
							tags
					))
					.toList();

            List<AnimeRecommendDto2> scoreOnlyList1 = recommendationMapper.selectRecentHighScoreOnly(scoreOnlyRequest);
            resultAnimes = scoreOnlyList1.stream()
                    .map(rec -> new TestAnimeItemDto(
                            rec.getAnimeId(),
                            rec.getTitle(),
                            rec.getCoverImageUrl(),
                            testMapper.getAnimeTags(rec.getAnimeId())
                    ))
                    .toList();
            animes = scoreOnlyList1;
            totalCount = recommendationMapper.countRecentHighScoreOnly(scoreOnlyRequest);

        } else {
            // tag based
            // 유저 리뷰 전부 불러와서 상위 20% & 최대 20개 애니 추출
            List<Long> topRatedAnimeIds = reviewUserMapper.findTopRatedAnimeIds(userId, topN);

            long totalReviews = reviewUserMapper.countReviewsByUser(userId);
			int percentLimit = (int) Math.ceil(totalReviews * (topPercent / 100.0));

            List<Long> topPercentIds = reviewUserMapper.findTopRatedAnimeIds(userId, percentLimit);

			List<Long> filteredIds = topRatedAnimeIds.stream()
					.filter(topPercentIds::contains)
					.toList();

            referenceAnimes = filteredIds.stream()
   					 .map(a -> {
							AnimeItemDto info = testMapper.selectReferenceAnime(a).get(0);
							List<TestAnimeTagDto> tags = testMapper.getAnimeTags(a);
							return new TestAnimeItemDto(
									info.getAnimeId(), info.getTitle(), info.getCoverImageUrl(), tags
							);
						})
					.toList();

            // 이 애니들에 등장한 태그별 (rating * weight) 합산 → 상위 10개 tagIds 추출
            List<TagScoreDto> tagScores = recommendationMapper.selectTagScoresForUser(
                    userId, filteredIds
            );
            List<Long> tagIds = tagScores.stream()
                    .sorted(Comparator.comparingDouble(TagScoreDto::getScore).reversed())
                    .limit(10)
                    .map(TagScoreDto::getTagId)
                    .toList();

            TagBasedScoreOnlyRequest cntReq = new TagBasedScoreOnlyRequest(
                    userId, tagIds,
                    lastCount, lastId, size
            );

            List<AnimeRecommendDto2> scoreOnlyList1 = recommendationMapper.selectTagBasedScoreOnly(cntReq);
            resultAnimes = scoreOnlyList1.stream()
                    .map(rec -> new TestAnimeItemDto(
                            rec.getAnimeId(),
                            rec.getTitle(),
                            rec.getCoverImageUrl(),
                            testMapper.getAnimeTags(rec.getAnimeId())
                    ))
                    .toList();
            animes = scoreOnlyList1;
            totalCount = recommendationMapper.countTagBasedScoreOnly(cntReq);
        }

        CursorDto cursor;

        if (animes.isEmpty()) {
            cursor = CursorDto.of((String) null, null, null);
        } else {
            Long nextId = animes.getLast().getAnimeId();
            Long nextScore = animes.getLast().getScore();
            cursor = CursorDto.of(null, nextId, String.valueOf(nextScore));
        }

        List<AnimeItemDto> items = animes
                .stream()
                .map(e -> new AnimeItemDto(e.getAnimeId(), e.getTitle(), e.getCoverImageUrl()))
                .toList();

        return UserMainRecommendationPageDto.of(totalCount, cursor, referenceAnimes, resultAnimes);
    }

    public UserMainRecommendationPageDto test3(Long lastValue, Long lastCount, Long lastId, Long size, Long userId, UserRecommendMode mode, int topN, double topPercent) {
        UserState state = userStateMapper.findByUserId(userId);
        if (state == null) {
            Long referenceAnimeId = reviewUserMapper.findMostRecentHighRatedAnime(userId);
            userStateMapper.insertInitialState(userId, UserRecommendMode.RECENT_HIGH, referenceAnimeId);
            state = new UserState(userId, UserRecommendMode.RECENT_HIGH, referenceAnimeId, LocalDateTime.now());
        }

        // RECENT_HIGH / TAG_BASED 전환 체크
        if (state.getMode() == UserRecommendMode.RECENT_HIGH) {
            long days = Duration.between(state.getStartDate(), LocalDateTime.now()).toDays();
            if (days >= 3) {
                userStateMapper.updateMode(userId, UserRecommendMode.TAG_BASED, null);
                state = userStateMapper.findByUserId(userId);
            } else {
                Long latestHigh = reviewUserMapper.findMostRecentHighRatedAnime(userId);
                if (latestHigh == null) {
                    // 리뷰가 없음
                    return UserMainRecommendationPageDto.of(0L, CursorDto.of((Long) null), List.of(), List.of());
                }
                if (!latestHigh.equals(state.getReferenceAnimeId())) {
                    userStateMapper.updateReferenceAnime(userId, latestHigh);
                    state.setReferenceAnimeId(latestHigh);
                }
            }
        }
        long totalCount = 0;
        List<AnimeRecommendDto2> animes;
        List<AnimeRecommendDto2> fullList;
        List<TestAnimeItemDto> referenceAnimes = new ArrayList<>();
        List<TestAnimeItemDto> resultAnimes  = new ArrayList<>();

        if (mode == UserRecommendMode.RECENT_HIGH) {
//            Long referenceAnimeId = state.getReferenceAnimeId();
            Long referenceAnimeId = reviewUserMapper.findMostRecentHighRatedAnime(userId);
            List<Long> tagIds = animeTagMapper.findTopTagsByAnime(referenceAnimeId, 5);
            RecentHighFullRequest fullRequest =
                    new RecentHighFullRequest(userId, referenceAnimeId, tagIds, lastValue, lastCount, lastId, size);

            List<AnimeItemDto> refAnimes = testMapper.selectReferenceAnime(referenceAnimeId);
            List<TestAnimeTagDto> tags = testMapper.getAnimeTags(referenceAnimeId);

            referenceAnimes = refAnimes.stream()
					.map(ref -> new TestAnimeItemDto(
							ref.getAnimeId(),
							ref.getTitle(),
							ref.getCoverImageUrl(),
							tags
					))
					.toList();

            List<AnimeRecommendDto2> fullList1 = recommendationMapper.selectRecentHighFull2(fullRequest);
            resultAnimes = fullList1.stream()
                    .map(rec -> new TestAnimeItemDto(
                            rec.getAnimeId(),
                            rec.getTitle(),
                            rec.getCoverImageUrl(),
                            testMapper.getAnimeTags(rec.getAnimeId())
                    ))
                    .toList();
            animes = fullList1;
            totalCount = recommendationMapper.countRecentHighFull(fullRequest);

        } else {
            // tag based
            List<Long> topRatedAnimeIds = reviewUserMapper.findTopRatedAnimeIds(userId, topN);
			long totalReviews = reviewUserMapper.countReviewsByUser(userId);
			int percentLimit = (int) Math.ceil(totalReviews * (topPercent / 100.0));
            List<Long> topPercentIds = reviewUserMapper.findTopRatedAnimeIds(userId, percentLimit);
			List<Long> filteredIds = topRatedAnimeIds.stream()
					.filter(topPercentIds::contains)
					.toList();
            referenceAnimes = filteredIds.stream()
   					 .map(a -> {
							AnimeItemDto info = testMapper.selectReferenceAnime(a).get(0);
							List<TestAnimeTagDto> tags = testMapper.getAnimeTags(a);
							return new TestAnimeItemDto(
									info.getAnimeId(), info.getTitle(), info.getCoverImageUrl(), tags
							);
						})
					.toList();

            List<TagScoreDto> tagScores = recommendationMapper.selectTagScoresForUser(
                    userId, filteredIds
            );
            List<Long> tagIds = tagScores.stream()
                    .sorted(Comparator.comparingDouble(TagScoreDto::getScore).reversed())
                    .limit(10)
                    .map(TagScoreDto::getTagId)
                    .toList();
            // Full 버전
            TagBasedFullRequest fullReq = new TagBasedFullRequest(
                    userId, tagIds,
                    lastValue, lastCount, lastId, size
            );
            List<AnimeRecommendDto2> fullList1 = recommendationMapper.selectTagBasedFull2(fullReq);
            resultAnimes = fullList1.stream()
                    .map(rec -> new TestAnimeItemDto(
                            rec.getAnimeId(),
                            rec.getTitle(),
                            rec.getCoverImageUrl(),
                            testMapper.getAnimeTags(rec.getAnimeId())
                    ))
                    .toList();
            animes = fullList1;
            totalCount = recommendationMapper.countTagBasedFull(fullReq);

        }

        CursorDto cursor;

        if (animes.isEmpty()) {
            cursor = CursorDto.of((String) null, null, null);
        } else {
            Long nextId = animes.getLast().getAnimeId();
            Long nextScore = animes.getLast().getScore();
            Long nextCount = animes.getLast().getTagCount();
            cursor = CursorDto.of(nextId, String.valueOf(nextScore), String.valueOf(nextCount));
        }

        List<AnimeItemDto> items = animes
                .stream()
                .map(e -> new AnimeItemDto(e.getAnimeId(), e.getTitle(), e.getCoverImageUrl()))
                .toList();

        return UserMainRecommendationPageDto.of(totalCount, cursor, referenceAnimes, resultAnimes);
    }

    public UserMainRecommendationPageDto test4(Long lastValue, Long lastCount, Long lastId, Long size, Long userId, UserRecommendMode mode, int topN, double topPercent) {
        UserState state = userStateMapper.findByUserId(userId);
        if (state == null) {
            Long referenceAnimeId = reviewUserMapper.findMostRecentHighRatedAnime(userId);
            userStateMapper.insertInitialState(userId, UserRecommendMode.RECENT_HIGH, referenceAnimeId);
            state = new UserState(userId, UserRecommendMode.RECENT_HIGH, referenceAnimeId, LocalDateTime.now());
        }

        // RECENT_HIGH / TAG_BASED 전환 체크
        if (state.getMode() == UserRecommendMode.RECENT_HIGH) {
            long days = Duration.between(state.getStartDate(), LocalDateTime.now()).toDays();
            if (days >= 3) {
                userStateMapper.updateMode(userId, UserRecommendMode.TAG_BASED, null);
                state = userStateMapper.findByUserId(userId);
            } else {
                Long latestHigh = reviewUserMapper.findMostRecentHighRatedAnime(userId);
                if (latestHigh == null) {
                    // 리뷰가 없음
                    return UserMainRecommendationPageDto.of(0L, CursorDto.of((Long) null), List.of(), null);
                }
                if (!latestHigh.equals(state.getReferenceAnimeId())) {
                    userStateMapper.updateReferenceAnime(userId, latestHigh);
                    state.setReferenceAnimeId(latestHigh);
                }
            }
        }
        long totalCount = 0;
        List<AnimeRecommendDto2> animes;
        List<AnimeRecommendDto2> fullList;
        List<TestAnimeItemDto> referenceAnimes = new ArrayList<>();
        List<TestAnimeItemDto> resultAnimes  = new ArrayList<>();

        if (state.getMode() == UserRecommendMode.RECENT_HIGH) {
//            Long referenceAnimeId = state.getReferenceAnimeId();
            Long referenceAnimeId = reviewUserMapper.findMostRecentHighRatedAnime(userId);
            List<Long> tagIds = animeTagMapper.findTopTagsByAnime(referenceAnimeId, 5);
            RecentHighFullRequest fullRequest =
                    new RecentHighFullRequest(userId, referenceAnimeId, tagIds, lastValue, lastCount, lastId, size);

            List<AnimeItemDto> refAnimes = testMapper.selectReferenceAnime(referenceAnimeId);
            List<TestAnimeTagDto> tags = testMapper.getAnimeTags(referenceAnimeId);

            referenceAnimes = refAnimes.stream()
					.map(ref -> new TestAnimeItemDto(
							ref.getAnimeId(),
							ref.getTitle(),
							ref.getCoverImageUrl(),
							tags
					))
					.toList();

            List<AnimeRecommendDto2> fullList1 = recommendationMapper.selectRecentHighFull(fullRequest);
            resultAnimes = fullList1.stream()
                    .map(rec -> new TestAnimeItemDto(
                            rec.getAnimeId(),
                            rec.getTitle(),
                            rec.getCoverImageUrl(),
                            testMapper.getAnimeTags(rec.getAnimeId())
                    ))
                    .toList();
            animes = fullList1;
            totalCount = recommendationMapper.countRecentHighFull(fullRequest);

        } else {
            // tag based
            List<Long> topRatedAnimeIds = reviewUserMapper.findTopRatedAnimeIds(userId, topN);

            long totalReviews = reviewUserMapper.countReviewsByUser(userId);
			int percentLimit = (int) Math.ceil(totalReviews * (topPercent / 100.0));

            List<Long> topPercentIds = reviewUserMapper.findTopRatedAnimeIds(userId, percentLimit);

			List<Long> filteredIds = topRatedAnimeIds.stream()
					.filter(topPercentIds::contains)
					.toList();

            referenceAnimes = filteredIds.stream()
   					 .map(a -> {
							AnimeItemDto info = testMapper.selectReferenceAnime(a).get(0);
							List<TestAnimeTagDto> tags = testMapper.getAnimeTags(a);
							return new TestAnimeItemDto(
									info.getAnimeId(), info.getTitle(), info.getCoverImageUrl(), tags
							);
						})
					.toList();

            List<TagScoreDto> tagScores = recommendationMapper.selectTagScoresForUser(
                    userId, filteredIds
            );
            List<Long> tagIds = tagScores.stream()
                    .sorted(Comparator.comparingDouble(TagScoreDto::getScore).reversed())
                    .limit(10)
                    .map(TagScoreDto::getTagId)
                    .toList();
            // Full 버전
            TagBasedFullRequest fullReq = new TagBasedFullRequest(
                    userId, tagIds,
                    lastValue, lastCount, lastId, size
            );
            List<AnimeRecommendDto2> fullList1 = recommendationMapper.selectTagBasedFull(fullReq);
            resultAnimes = fullList1.stream()
                    .map(rec -> new TestAnimeItemDto(
                            rec.getAnimeId(),
                            rec.getTitle(),
                            rec.getCoverImageUrl(),
                            testMapper.getAnimeTags(rec.getAnimeId())
                    ))
                    .toList();
            animes = fullList1;
            totalCount = recommendationMapper.countTagBasedFull(fullReq);
        }

        CursorDto cursor;

        if (animes.isEmpty()) {
            cursor = CursorDto.of((String) null, null, null);
        } else {
            Long nextId = animes.getLast().getAnimeId();
            Long nextScore = animes.getLast().getScore();
            Long nextCount = animes.getLast().getTagCount();
            cursor = CursorDto.of(nextId, String.valueOf(nextScore), String.valueOf(nextCount));
        }

        List<AnimeItemDto> items = animes
                .stream()
                .map(e -> new AnimeItemDto(e.getAnimeId(), e.getTitle(), e.getCoverImageUrl()))
                .toList();

        return UserMainRecommendationPageDto.of(totalCount, cursor, referenceAnimes, resultAnimes);
    }

}