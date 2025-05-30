package com.anipick.backend.recommendation.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.recommendation.domain.UserRecommendMode;
import com.anipick.backend.recommendation.domain.UserState;
import com.anipick.backend.recommendation.dto.AnimeRecommendDto;
import com.anipick.backend.recommendation.dto.RecentHighRequestDto;
import com.anipick.backend.recommendation.dto.TagBasedRequestDto;
import com.anipick.backend.recommendation.dto.UserMainRecommendationPageDto;
import com.anipick.backend.recommendation.mapper.RecommendationMapper;
import com.anipick.backend.recommendation.mapper.ReviewUserMapper;
import com.anipick.backend.recommendation.mapper.UserStateMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {
	private final UserStateMapper userStateMapper;
	private final ReviewUserMapper reviewUserMapper;
	private final RecommendationMapper recommendationMapper;

	public UserMainRecommendationPageDto getRecommendationAnimes(
		Long userId,
		Long lastScore,
		Long lastId,
		Long size
	) {
		// 유저 상태 조회
		UserState state = userStateMapper.findByUserId(userId);
		if (state == null) {
			Long refAnimeId = reviewUserMapper.findMostRecentHighRatedAnime(userId);
			userStateMapper.insertInitialState(userId, UserRecommendMode.RECENT_HIGH, refAnimeId);
			state = new UserState(userId, UserRecommendMode.RECENT_HIGH, refAnimeId, LocalDateTime.now());
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
					return UserMainRecommendationPageDto.emptyReviewOf(0L, List.of());
				}
				if (!latestHigh.equals(state.getReferenceAnimeId())) {
					userStateMapper.updateReferenceAnime(userId, latestHigh);
					state.setReferenceAnimeId(latestHigh);
				}
			}
		}

		long totalCount;
		List<AnimeRecommendDto> animes;

		// 모드별 추천 실행
		if (state.getMode() == UserRecommendMode.RECENT_HIGH) {
			RecentHighRequestDto req =
				new RecentHighRequestDto(userId, state.getReferenceAnimeId(), lastScore, lastId, size);
			animes = recommendByRecentHigh(req);
			totalCount = recommendationMapper.countByRecentHigh(req);
		} else {
			List<Long> topAnimeIds = reviewUserMapper.findTopRatedAnimeIds(userId, 20);
			TagBasedRequestDto req =
				new TagBasedRequestDto(userId, topAnimeIds, lastScore, lastId, size);
			animes = recommendByTagBased(req);
			totalCount = recommendationMapper.countByTagBased(req);
		}

		Long nextId;
		Long nextScore;

		if (animes.isEmpty()) {
			nextId = null;
			nextScore = null;
		} else {
			nextId = animes.getLast().getAnimeId();
			nextScore = animes.getLast().getScore();
		}

		CursorDto cursor = CursorDto.of(null, nextId, String.valueOf(nextScore));

		List<AnimeItemDto> items = animes
			.stream()
			.map(e -> new AnimeItemDto(e.getAnimeId(), e.getTitle(), e.getCoverImageUrl()))
			.toList();

		return UserMainRecommendationPageDto.of(totalCount, cursor, items);
	}

	private List<AnimeRecommendDto> recommendByRecentHigh(RecentHighRequestDto req) {
		return recommendationMapper.recommendByRecentHigh(req);
	}

	private List<AnimeRecommendDto> recommendByTagBased(TagBasedRequestDto req) {
		return recommendationMapper.recommendByTagBased(req);
	}
}
