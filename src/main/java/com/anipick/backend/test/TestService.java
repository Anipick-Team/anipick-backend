package com.anipick.backend.test;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.search.dto.SearchAnimePageDto;
import com.anipick.backend.search.mapper.SearchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

	private final SearchMapper mapper;
    private final TestMapper testMapper;

    public List<TestResponseDto> findReviews(long userId) {
        return testMapper.findReviews(userId);
    }

    public SearchAnimePageDto findSearchAnimes(String query, Long lastId, Long size) {

		long totalCount = mapper.countSearchAnime(query);

		List<AnimeItemDto> items = mapper.selectSearchAnimes(query, lastId, size);

		Long nextId;

		if (items.isEmpty()) {
			nextId = null;
		} else {
			nextId = items.getLast().getAnimeId();
		}

		CursorDto cursor = CursorDto.of(nextId);

		return new SearchAnimePageDto(totalCount, 0, 0, cursor, items);
	}

	public TestUserRecommendationStateDto findRecommendationState(long userId) {
		return testMapper.findRecommendationState(userId);
	}
}
