package com.anipick.backend.anime.service;

import com.anipick.backend.anime.domain.AnimeFormat;
import com.anipick.backend.anime.domain.Season;
import com.anipick.backend.anime.dto.GenreDto;
import com.anipick.backend.anime.dto.MetaDataGroupResultDto;
import com.anipick.backend.anime.dto.SeasonDto;
import com.anipick.backend.anime.mapper.MetaDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MetaDataService {

    private final MetaDataMapper mapper;

    public MetaDataGroupResultDto getMetaDataGroup() {
        int thisYear = Year.now().getValue();

        List<Integer> years = IntStream.rangeClosed(1940, thisYear + 1)
                .boxed().sorted(Comparator.reverseOrder())
                .toList();

        List<SeasonDto> seasons = Arrays.stream(Season.values())
                .map(SeasonDto::from)
                .collect(Collectors.toList());

        List<GenreDto> genreDtos = mapper.selectAllGenres();
        List<AnimeFormat> types = Arrays.stream(AnimeFormat.values()).toList();

        return new MetaDataGroupResultDto(years, seasons, genreDtos, types);
    }
}
