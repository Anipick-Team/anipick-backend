package com.anipick.backend.explore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.*;

import java.util.ArrayList;
import java.util.List;

import com.anipick.backend.explore.domain.GenresOption;
import com.anipick.backend.explore.mapper.ExploreMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExploreServiceTest {

	@Mock
	private ExploreMapper mapper;

	@InjectMocks
	private ExploreService service;

	@Captor
	private ArgumentCaptor<List<String>> formatsCaptor;

	@Test
	@DisplayName("TYPE Parameter 변환 확인")
	void typeConversionSuccess() {
		// given
		doReturn(List.of())
			.when(mapper).selectExplored(
				eq(2024), eq(1), anyList(), eq(1),
				anyList(), eq(3),
				eq("OR"), eq("TVA"),
				eq("popularity"), anyString(),
				eq(3L), eq(44), eq(5)
			);

		List<Long> genres = new ArrayList<>();
		genres.add(3L);

		// when
		service.explore(
			2024, 1,
			genres, GenresOption.OR,
			"TVA", "popularity",
			3L, 44,
			5
		);

		// then
		then(mapper).should().selectExplored(
			eq(2024), eq(1), anyList(), eq(1),
			formatsCaptor.capture(), eq(3),
			eq("OR"), eq("TVA"),
			eq("popularity"), anyString(),
			eq(3L), eq(44), eq(5)
		);

		assertThat(formatsCaptor.getValue())
			.containsExactly("TV", "TV_SHORT", "ONA");
	}
}
