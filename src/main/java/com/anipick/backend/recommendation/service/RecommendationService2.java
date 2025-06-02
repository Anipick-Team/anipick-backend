package com.anipick.backend.recommendation.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import com.anipick.backend.recommendation.dto.*;
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

	public UserMainRecommendationPageDto getRecommendationAnimes(
		Long lastValue, Long lastCount, Long lastId, Long size, Long userId
	) {
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
					return UserMainRecommendationPageDto.of(0L, CursorDto.of((Long)null), List.of());
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
		List<AnimeRecommendDto2> countOnlyList;
		List<AnimeRecommendDto2> scoreOnlyList;

		if (state.getMode() == UserRecommendMode.RECENT_HIGH) {
			List<Long> tagIds = animeTagMapper.findTopTagsByAnime(state.getReferenceAnimeId(), 5);
			RecentHighFullRequest fullRequest =
				new RecentHighFullRequest(userId, state.getReferenceAnimeId(), tagIds, lastValue, lastCount, lastId, size);

			fullList = recommendationMapper.selectRecentHighFull(fullRequest);

			// Count-Only 버전 (tag_count 만 반환)
			// RecentHighCountOnlyRequest countOnlyRequest =
			// 	new RecentHighCountOnlyRequest(userId, state.getReferenceAnimeId(), tagIds, lastCount, lastId, size);
			//
			// countOnlyList = recommendationMapper.selectRecentHighCountOnly(countOnlyRequest);

			// Score-Only 버전 (total_score 만 반환)
			// RecentHighScoreOnlyRequest scoreOnlyRequest =
			// 	new RecentHighScoreOnlyRequest(userId, state.getReferenceAnimeId(), tagIds, lastValue, lastId, size);
			//
			// scoreOnlyList = recommendationMapper.selectRecentHighScoreOnly(scoreOnlyRequest);

			// 버전 골라 사용할 것
			animes = fullList;

			totalCount = recommendationMapper.countRecentHighFull(fullRequest);

		} else {
			// tag based

			// 유저 리뷰 전부 불러와서 상위 20% & 최대 20개 애니 추출
			List<Long> topRatedAnimeIds = reviewUserMapper.findUserReviews(userId);
			System.out.println("topRatedAnimeIds = " + topRatedAnimeIds);

			// 이 애니들에 등장한 태그별 (rating * weight) 합산 → 상위 10개 tagIds 추출
			List<TagScoreDto> tagScores = recommendationMapper.selectTagScoresForUser(
				userId, topRatedAnimeIds
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
			fullList = recommendationMapper.selectTagBasedFull(fullReq);
			totalCount = recommendationMapper.countTagBasedFull(fullReq);

			// Count-Only 버전
			// TagBasedCountOnlyRequest cntReq = new TagBasedCountOnlyRequest(
			// 	userId, tagIds,
			// 	lastCount, lastId, size
			// );
			// countOnlyList =
			// 	recommendationMapper.selectTagBasedCountOnly(cntReq);
			// totalCount = recommendationMapper.countTagBasedCountOnly(cntReq);

			// Score-Only 버전
			// TagBasedScoreOnlyRequest scReq = new TagBasedScoreOnlyRequest(
			// 	userId, tagIds,
			// 	lastValue, lastId, size
			// );
			//  scoreOnlyList =
			// 	recommendationMapper.selectTagBasedScoreOnly(scReq);
			// totalCount = recommendationMapper.countTagBasedScoreOnly(scReq);

			animes = fullList;
		}

		Long nextId;
		if (animes.isEmpty()) {
			nextId = null;
		} else {
			nextId = animes.getLast().getAnimeId();
		}

		Long nextScore;
		if (animes.isEmpty()) {
			nextScore = null;
		} else {
			nextScore = animes.getLast().getScore();
		}

		Long nextCount;
		if (animes.isEmpty()) {
			nextCount = null;
		} else {
			nextCount = animes.getLast().getTagCount();
		}
		// full 일 경우
		CursorDto cursor = CursorDto.of(nextId, String.valueOf(nextScore), String.valueOf(nextCount));
		// score 일 경우
		// CursorDto cursor = CursorDto.of(null, nextId, String.valueOf(nextScore));
		// count 일 경우
		// CursorDto cursor = CursorDto.of(null, nextId, String.valueOf(nextCount));

		List<AnimeItemDto> items = animes
			.stream()
			.map(e -> new AnimeItemDto(e.getAnimeId(), e.getTitle(), e.getCoverImageUrl()))
			.toList();

		return UserMainRecommendationPageDto.of(totalCount, cursor, items);
	}

	public UserMainRecommendationPageDto test1(Long lastValue, Long lastCount, Long lastId, Long size, Long userId) {
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
					return UserMainRecommendationPageDto.of(0L, CursorDto.of((Long)null), List.of());
				}
				if (!latestHigh.equals(state.getReferenceAnimeId())) {
					userStateMapper.updateReferenceAnime(userId, latestHigh);
					state.setReferenceAnimeId(latestHigh);
				}
			}
		}
		long totalCount = 0;
		List<AnimeRecommendDto2> animes;


	};
	public UserMainRecommendationPageDto test2(Long lastValue, Long lastCount, Long lastId, Long size, Long userId) {

	};
	public UserMainRecommendationPageDto test3(Long lastValue, Long lastCount, Long lastId, Long size, Long userId) {

	};
	public UserMainRecommendationPageDto test4(Long lastValue, Long lastCount, Long lastId, Long size, Long userId) {

	};
}