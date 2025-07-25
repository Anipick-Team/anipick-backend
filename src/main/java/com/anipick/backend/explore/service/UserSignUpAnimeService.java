package com.anipick.backend.explore.service;

import com.anipick.backend.anime.domain.RangeDate;
import com.anipick.backend.anime.domain.SeasonConverter;
import com.anipick.backend.anime.mapper.MetaDataMapper;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.explore.dto.*;
import com.anipick.backend.explore.mapper.UserSignUpAnimeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSignUpAnimeService {

    private final UserSignUpAnimeMapper userSignUpAnimeMapper;
    private final MetaDataMapper metaDataMapper;

    public UserSignUpAnimePageDto getAnimeExploreSearch(
            String query,
            Integer year,
            Integer season,
            Long genres,
            Long lastId,
            int size
    ) {
        log.debug("Anime explore search log : 년도={}, 분기={}, 장르 ID={}, 마지막 ID={}, 사이즈={}",
                year, season, genres, lastId, size
        );
        String genresName;
        if (genres != null) {
            genresName = metaDataMapper.selectGenresById(genres);
        } else {
            genresName = null;
        }

        String nowString = LocalDate.now().toString();

        SignUpAnimeExploreSearchRequestDto requestDto =
                makeExploreSearchRequestDto(query, year, season, genres, lastId, size, nowString);

        long totalCount = userSignUpAnimeMapper.countExploredAndSearch(requestDto);


        List<SignUpPopularAnimeItemDto> items = userSignUpAnimeMapper.
                selectAnimeExploredAndSearch(requestDto);

        List<SignUpAnimeItemDto> result = new ArrayList<>();

        if (!items.isEmpty()) {
            List<Long> animeIds = items.stream()
                    .map(SignUpPopularAnimeItemDto::getAnimeId)
                    .toList();

            List<AnimeIdGenreDto> genreItems = userSignUpAnimeMapper.selectGenresByAnimeIds(animeIds);

            Map<Long, List<String>> genreMap = new HashMap<>();

            for (AnimeIdGenreDto dto : genreItems) {
                Long animeId = dto.getAnimeId();
                String genre = dto.getGenre();

                if (!genreMap.containsKey(animeId)) {
                    genreMap.put(animeId, new ArrayList<>());
                }

                genreMap.get(animeId).add(genre);
            }

            items.forEach(item ->
                    item.setGenres(genreMap.getOrDefault(item.getAnimeId(), List.of()))
            );
        } else {
            return UserSignUpAnimePageDto.of(totalCount, CursorDto.of(null), result);
        }

        result = items.stream()
                .map(dto -> {
                    List<String> genreList = new ArrayList<>();
                    // 들어있는 장르가 null이 아니면서 3개 이상일 경우
                    genreList = anime3GentePick(genres, dto, genreList, genresName);
                    return SignUpAnimeItemDto.of(
                            dto.getAnimeId(),
                            dto.getTitle(),
                            dto.getCoverImageUrl(),
                            genreList
                    );
                })
                .toList();

        Long nextId;
        if (items.isEmpty()) {
            nextId = null;
        } else {
            nextId = items.getLast().getScore();
        }

        CursorDto cursor = CursorDto.of(nextId);

        return UserSignUpAnimePageDto.of(totalCount, cursor, result);
    }

    private static List<String> anime3GentePick(Long genres, SignUpPopularAnimeItemDto dto, List<String> genreList, String genresName) {
        if (dto.getGenres() != null) {
            genreList.addAll(dto.getGenres());
        }

        if (genreList.size() >= 3) {
            List<String> genreTop3 = new ArrayList<>();
            genreTop3.add(genreList.get(0));
            genreTop3.add(genreList.get(1));
            genreTop3.add(genreList.get(2));

            if (genres != null && !genreTop3.contains(genresName)) {
                genreTop3.set(2, genresName);
            }
            genreList = genreTop3;
        }
        return genreList;
    }

    private static SignUpAnimeExploreSearchRequestDto makeExploreSearchRequestDto(
            String query, Integer year, Integer season, Long genres, Long lastId, Integer size, String nowString
    ) {
        if (year != null && season != null) {
            RangeDate dateRange = SeasonConverter.getRangDate(year, season);
            return SignUpAnimeExploreSearchRequestDto.of(
                    dateRange,
                    query,
                    genres,
                    lastId,
                    size,
                    nowString
            );
        } else if (year != null) {
            RangeDate dateRange = SeasonConverter.getYearRangDate(year);
            return SignUpAnimeExploreSearchRequestDto.of(
                    dateRange,
                    query,
                    genres,
                    lastId,
                    size,
                    nowString
            );
        }
        return SignUpAnimeExploreSearchRequestDto.dateNullOf(
                query,
                genres,
                lastId,
                size,
                nowString
        );
    };
}
