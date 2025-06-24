package com.anipick.backend.home.service;

import com.anipick.backend.anime.dto.ComingSoonItemBasicDto;
import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.home.dto.HomeComingSoonItemDto;
import com.anipick.backend.home.dto.HomeRecentReviewItemDto;
import com.anipick.backend.home.mapper.HomeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeMapper homeMapper;

    private static final DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd");

    @Value("${anime.default-cover-url}")
	private String defaultCoverUrl;

    @Transactional(readOnly = true)
    public List<HomeRecentReviewItemDto> getRecentReviews(Long userId) {
        List<HomeRecentReviewItemDto> raws = homeMapper.selectHomeRecentReviews(userId, 10);

        List<HomeRecentReviewItemDto> items = raws.stream()
                .map(dto -> {
                    LocalDateTime dateTime = LocalDateTime.parse(dto.getCreatedAt(), parser);
                    String formattedDate = dateTime.format(formatter);

                    return HomeRecentReviewItemDto.of(
                            dto.getReviewId(),
                            dto.getAnimeId(),
                            dto.getAnimeTitle(),
                            dto.getReviewContent(),
                            dto.getNickname(),
                            formattedDate
                    );
                })
                .toList();
        return items;
    }

    @Transactional(readOnly = true)
    public List<HomeComingSoonItemDto> getComingSoonAnimes() {
        SortOption sortOption = SortOption.LATEST;
        String orderByQuery = sortOption.getOrderByQuery();
        List<ComingSoonItemBasicDto> comingSoonItemBasicDtos = homeMapper.selectHomeComingSoonAnimes(defaultCoverUrl, orderByQuery, 10);

        List<ComingSoonItemBasicDto> typeToReleaseDateList = comingSoonItemBasicDtos.stream()
                .map(ComingSoonItemBasicDto::typeToReleaseDate)
                .toList();

        List<HomeComingSoonItemDto> items = typeToReleaseDateList.stream()
                .map(dto -> HomeComingSoonItemDto.of(
                        dto.getAnimeId(),
                        dto.getTitle(),
                        dto.getCoverImageUrl(),
                        dto.getStartDate()
                ))
                .toList();
        return items;
    }
}
