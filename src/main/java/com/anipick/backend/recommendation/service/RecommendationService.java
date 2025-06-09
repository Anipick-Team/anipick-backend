package com.anipick.backend.recommendation.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.anipick.backend.test.TestAnimeItemDto;
import com.anipick.backend.test.TestAnimeTagDto;
import com.anipick.backend.test.TestMapper;
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
	private final TestMapper testMapper;

	public UserMainRecommendationPageDto getRecommendationAnimes(
			Long userId,
			Long lastScore,
			Long lastId,
			Long size,
			UserRecommendMode mode,
			int topN,
			double topPercent
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
		List<TestAnimeItemDto> referenceAnimes = new ArrayList<>();
		List<TestAnimeItemDto> resultAnimes;
		List<AnimeRecommendDto> animes;

		// 모드별 추천 실행
		if (mode == UserRecommendMode.RECENT_HIGH) {
			Long referenceAnimeId = state.getReferenceAnimeId();
			RecentHighRequestDto req =
				new RecentHighRequestDto(userId, referenceAnimeId, lastScore, lastId, size);

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

			List<AnimeRecommendDto> animes1 = recommendByRecentHigh(req);

			resultAnimes = animes1.stream()
					.map(rec -> new TestAnimeItemDto(
							rec.getAnimeId(),
							rec.getTitle(),
							rec.getCoverImageUrl(),
							testMapper.getAnimeTags(rec.getAnimeId())
					))
					.toList();
			animes = animes1;
			totalCount = recommendationMapper.countByRecentHigh(req);
		} else {
			//tag based
			List<Long> topNIds = reviewUserMapper.findTopRatedAnimeIds(userId, topN);

			long totalReviews = reviewUserMapper.countReviewsByUser(userId);

			int percentLimit = (int) Math.ceil(totalReviews * (topPercent / 100.0));
			List<Long> topPercentIds = reviewUserMapper.findTopRatedAnimeIds(userId, percentLimit);

			List<Long> filteredIds = topNIds.stream()
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

			TagBasedRequestDto req =
				new TagBasedRequestDto(userId, filteredIds, lastScore, lastId, size);

			List<AnimeRecommendDto> animes1 = recommendByTagBased(req);
			resultAnimes = animes1.stream()
					.map(rec -> new TestAnimeItemDto(
							rec.getAnimeId(),
							rec.getTitle(),
							rec.getCoverImageUrl(),
							testMapper.getAnimeTags(rec.getAnimeId())
					))
					.toList();
			animes = animes1;
			totalCount = recommendationMapper.countByTagBased(req);
		}

		CursorDto cursor;

		if (animes.isEmpty()) {
			cursor = CursorDto.of((String) null, null, null);
		} else {
			Long nextId = animes.getLast().getAnimeId();
			Long nextScore = animes.getLast().getScore();
			cursor = CursorDto.of(null, nextId, nextScore.toString());
		}

		List<AnimeItemDto> items = animes
			.stream()
			.map(e -> new AnimeItemDto(e.getAnimeId(), e.getTitle(), e.getCoverImageUrl()))
			.toList();

		return UserMainRecommendationPageDto.of(totalCount, cursor, referenceAnimes, resultAnimes);
	}

	private List<AnimeRecommendDto> recommendByRecentHigh(RecentHighRequestDto req) {
		return recommendationMapper.recommendByRecentHigh(req);
	}

	private List<AnimeRecommendDto> recommendByTagBased(TagBasedRequestDto req) {
		return recommendationMapper.recommendByTagBased(req);
	}
}
