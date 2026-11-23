package com.anipick.backend.anime.service;

import com.anipick.backend.anime.domain.AnimeStatus;
import com.anipick.backend.anime.dto.AnimeDetailInfoItemDto;
import com.anipick.backend.anime.dto.AnimeDetailInfoResultDto;
import com.anipick.backend.anime.dto.AnimeDetailYearSeasonRankingDto;
import com.anipick.backend.anime.mapper.AnimeMapper;
import com.anipick.backend.anime.mapper.GenreMapper;
import com.anipick.backend.anime.mapper.StudioMapper;
import com.anipick.backend.ranking.service.RankingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnimeServiceInfoDetailTest {
    private static final Long ANIME_ID = 10001L;
    private static final Long USER_ID = 1L;

    @Mock
    AnimeMapper animeMapper;
    @Mock
    AnimeCacheService animeCacheService;
    @Mock
    GenreMapper genreMapper;
    @Mock
    StudioMapper studioMapper;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    RedisTemplate<String, Object> redisTemplate;
    @Mock
    RankingService rankingService;

    AnimeService sut;

    @BeforeEach
    void setUp() {
        sut = new AnimeService(animeMapper, animeCacheService, genreMapper, studioMapper, objectMapper, redisTemplate, rankingService);
        given(genreMapper.selectGenresByAnimeId(ANIME_ID)).willReturn(List.of());
        given(studioMapper.selectStudiosByAnimeId(ANIME_ID)).willReturn(List.of());
    }

    private void givenDetail(LocalDate startDate) {
        AnimeDetailInfoItemDto item = mock(AnimeDetailInfoItemDto.class);
        given(item.getStartDate()).willReturn(startDate);
        given(item.getStatus()).willReturn(AnimeStatus.RELEASING);
        given(animeMapper.selectAnimeInfoDetail(ANIME_ID, USER_ID)).willReturn(item);
    }

    @Test
    @DisplayName("방영 시작일이 있고 랭킹 대상이면 방영 시작 연도·분기와 순위를 반환한다")
    void yearSeasonRanking() {
        // given
        givenDetail(LocalDate.of(2025, 8, 15));
        given(rankingService.getYearSeasonRank(ANIME_ID, 2025, 3)).willReturn(2L);

        // when
        AnimeDetailInfoResultDto result = sut.getAnimeInfoDetail(ANIME_ID, USER_ID);

        // then
        AnimeDetailYearSeasonRankingDto ranking = result.getYearSeasonRanking();
        assertThat(result.getAirDate()).isEqualTo("2025년 3분기");
        assertThat(ranking).isNotNull();
        assertThat(ranking.getYear()).isEqualTo(2025);
        assertThat(ranking.getSeason()).isEqualTo(3);
        assertThat(ranking.getRank()).isEqualTo(2L);
    }

    @Test
    @DisplayName("방영 미정(방영 시작일 없음) 애니는 순위를 조회하지 않고 null을 반환한다")
    void unknownStartDate() {
        // given
        givenDetail(null);

        // when
        AnimeDetailInfoResultDto result = sut.getAnimeInfoDetail(ANIME_ID, USER_ID);

        // then
        assertThat(result.getAirDate()).isEqualTo("미정");
        assertThat(result.getYearSeasonRanking()).isNull();
        verify(rankingService, never()).getYearSeasonRank(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("성인·장르 없음으로 랭킹 제외 대상이면(순위 null) null을 반환한다")
    void excludedFromRanking() {
        // given
        givenDetail(LocalDate.of(2025, 1, 1));
        given(rankingService.getYearSeasonRank(ANIME_ID, 2025, 1)).willReturn(null);

        // when
        AnimeDetailInfoResultDto result = sut.getAnimeInfoDetail(ANIME_ID, USER_ID);

        // then
        assertThat(result.getYearSeasonRanking()).isNull();
    }
}
