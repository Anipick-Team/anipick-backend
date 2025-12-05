package com.anipick.backend.anime.service;

import com.anipick.backend.anime.domain.*;
import com.anipick.backend.anime.dto.*;
import com.anipick.backend.anime.mapper.AnimeMapper;
import com.anipick.backend.anime.mapper.GenreMapper;
import com.anipick.backend.anime.mapper.StudioMapper;
import com.anipick.backend.anime.util.FormatConvert;
import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.util.LocalizationUtil;
import com.anipick.backend.image.domain.ImageDefaults;
import com.anipick.backend.search.dto.StudioItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimeService {
    private final AnimeMapper mapper;
    private final GenreMapper genreMapper;
    private final StudioMapper studioMapper;
    private static final int ITEM_DEFAULT_SIZE = 10;
    @Value("${anime.default-cover-url}")
    private String defaultCoverUrl;

    private static final DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd");
    private static final String UNKNOWN_AIR_DATE = "미정";

    /**
     * 다음 시즌 방영 예정 애니
     */
    public UpcomingSeasonResultDto getUpcomingSeasonAnimes() {
        LocalDate now = LocalDate.now();
        Season nextSeason = Season.getNextSeason(now);
        RangeDate nextSeasonRangDate = SeasonConverter.getNextSeasonRangDate(now);

        String startDate = nextSeasonRangDate.getStartDate();
        String endDate = nextSeasonRangDate.getEndDate();

        int season = nextSeason.getCode();
        String yearSubString = startDate.substring(0, 4);
        int seasonYear = Integer.parseInt(yearSubString);

        RangeDateRequestDto rangeDateRequestDto = RangeDateRequestDto
                .builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<AnimeItemDto> nextSeasonAnimes = mapper.selectUpcomingSeasonAnimes(rangeDateRequestDto)
                .stream()
                .map(AnimeItemDto::animeTitleTranslationPick)
                .collect(Collectors.toList());

        Collections.shuffle(nextSeasonAnimes);

        if (nextSeasonAnimes.size() > 10) {
            List<AnimeItemDto> animes10SubList = nextSeasonAnimes.subList(0, 10);
            return UpcomingSeasonResultDto.of(season, seasonYear, animes10SubList);
        }
        return UpcomingSeasonResultDto.of(season, seasonYear, nextSeasonAnimes);
    }

    /**
     * 공개예정 애니
     *
     * @param sort      정렬 (latest, popularity, startDate)
     * @param lastId    마지막 ID
     * @param size      개수
     * @param lastValue 정렬이 startDate일 때 문자열 날짜 (ex. "2025-08-09")
     */
    public ComingSoonPageDto getComingSoonAnimes(String sort, Long lastId, Long size, String lastValue) {
        SortOption sortOption = SortOption.of(sort);
        String orderByQuery = sortOption.getOrderByQuery();

        ComingSoonRequestDto comingSoonRequestDto =
                ComingSoonRequestDto.of(lastId, lastValue, size, orderByQuery, defaultCoverUrl);

        long totalCount = mapper.countComingSoon(comingSoonRequestDto);


        switch (sortOption) {
            case LATEST -> {
                return getSortLatestComingSoonAnimes(sort, comingSoonRequestDto, totalCount);
            }
            case POPULARITY -> {
                return getSortPopularityComingSoonAnimes(sort, comingSoonRequestDto, totalCount);
            }
            default -> {
                return getSortStartDateComingSoonAnimes(sort, comingSoonRequestDto, totalCount);
            }
        }
    }

    /**
     * 애니 상세에서 리뷰보기
     *
     * @param animeId   애니 ID
     * @param userId    로그인 중인 유저 ID
     * @param sort      정렬 (latest, likes, ratingDesc, ratingAsc)
     * @param lastId    마지막 ID (최신순일 경우, lastValue는 사용하지 않는다.)
     * @param lastValue 좋아요순일 때(ex. "1234"), 평점순일 때(ex. "4.5")
     * @param size      개수
     */
    public AnimeDetailInfoReviewsPageDto getAnimeInfoReviews(Long animeId, Long userId, String sort, Long lastId, String lastValue, int size) {
        SortOption sortOption = SortOption.of(sort);
        String orderByQuery = sortOption.getOrderByQuery();

        AnimeDetailInfoReviewsRequestDto reviewsRequestDto =
                AnimeDetailInfoReviewsRequestDto.of(animeId, userId, sort, orderByQuery, lastId, lastValue, size);

        long totalCount = mapper.selectAnimeReviewCount(animeId);

        List<AnimeDetailInfoReviewsItemDto> raws = mapper.selectAnimeDetailInfoReviews(reviewsRequestDto);

        List<AnimeDetailInfoReviewsResultDto> convertCreatedAtItems = raws.stream()
                .map(dto -> {
                    LocalDateTime dateTime = LocalDateTime.parse(dto.getCreatedAt(), parser);
                    String formattedDate = dateTime.format(formatter);

                    String imageIdStr = dto.getProfileImageUrl();
                    if (imageIdStr == null) {
                        imageIdStr = "-1";
                    }
                    Long imageId = Long.parseLong(imageIdStr);

                    String imageUrlEndpoint = getImageUrlEndpoint(imageId);

                    return AnimeDetailInfoReviewsResultDto.of(
                            dto.getReviewId(),
                            dto.getUserId(),
                            dto.getNickname(),
                            imageUrlEndpoint,
                            dto.getRating(),
                            dto.getContent(),
                            formattedDate,
                            dto.getIsSpoiler(),
                            dto.getLikeCount(),
                            dto.getIsLiked(),
                            dto.getIsMine()
                    );
                })
                .toList();
        switch (sortOption) {
            // 최신 순
            case LATEST -> {
                return getSortLatestAnimeDetailReviews(totalCount, convertCreatedAtItems, sort);
            }
            // 평점 높은 순
            case RATING_DESC -> {
                return getSortRatingDescAnimeDetailReviews(totalCount, convertCreatedAtItems, sort);
            }
            // 평점 낮은 순
            case RATING_ASC -> {
                return getSortRatingAscAnimeDetailReviews(totalCount, convertCreatedAtItems, sort);
            }
            // 좋아요 순
            default -> {
                return getSortLikesAnimeDetailReviews(totalCount, convertCreatedAtItems, sort);
            }
        }
    }

    /**
     * 애니 상세의 애니 정보
     *
     * @param animeId 애니 ID
     * @param userId  로그인 중인 유저 ID
     */
    public AnimeDetailInfoResultDto getAnimeInfoDetail(Long animeId, Long userId) {
        AnimeDetailInfoItemDto animeDetailInfoItemDto = mapper.selectAnimeInfoDetail(animeId, userId);

        String airDate;
        if (animeDetailInfoItemDto.getStartDate() == null) {
            airDate = "미정";
        } else {
            LocalDate startDate = animeDetailInfoItemDto.getStartDate();
            Season season = Season.containsSeason(startDate);
            airDate = animeDetailInfoItemDto.getStartDate().getYear() + "년 " + season.getName();
        }

        Long episodeCount;
        if (animeDetailInfoItemDto.getEpisode() == null) {
            episodeCount = 0L;
        } else {
            episodeCount = animeDetailInfoItemDto.getEpisode();
        }

        String type = FormatConvert.toClientType(animeDetailInfoItemDto.getType());

        List<GenreDto> genres = genreMapper.selectGenresByAnimeId(animeId);

        List<StudioItemDto> studios = studioMapper.selectStudiosByAnimeId(animeId)
                .stream()
                .map(StudioItemDto::studioNameTranslationPick)
                .toList();

        String pickTitle = LocalizationUtil.pickTitle(
                animeDetailInfoItemDto.getTitleKor(),
                animeDetailInfoItemDto.getTitleEng(),
                animeDetailInfoItemDto.getTitleRom(),
                animeDetailInfoItemDto.getTitleNat()
        );

        return AnimeDetailInfoResultDto.builder()
                .animeId(animeDetailInfoItemDto.getAnimeId())
                .title(pickTitle)
                .coverImageUrl(animeDetailInfoItemDto.getCoverImageUrl())
                .bannerImageUrl(animeDetailInfoItemDto.getBannerImageUrl())
                .description(animeDetailInfoItemDto.getDescription())
                .averageRating(animeDetailInfoItemDto.getAverageRatingAsString())
                .isLiked(animeDetailInfoItemDto.getIsLiked())
                .watchStatus(animeDetailInfoItemDto.getWatchStatus())
                .type(type)
                .reviewCount(animeDetailInfoItemDto.getReviewCount())
                .genres(genres)
                .episode(episodeCount)
                .airDate(airDate)
                .status(animeDetailInfoItemDto.getStatus().getStatusName())
                .age(animeDetailInfoItemDto.getAge())
                .studios(studios)
                .build();
    }

    /**
     * 애니 상세의 함께 볼만한 작품
     *
     * @param animeId 애니 ID
     */
    public List<AnimeItemDto> getAnimeRecommendation(Long animeId) {
        List<AnimeItemDto> items = mapper.selectAnimeInfoRecommendationsByAnimeId(animeId, ITEM_DEFAULT_SIZE)
                .stream()
                .map(AnimeItemDto::animeTitleTranslationPick)
                .toList();
        return items;
    }

    /**
     * 애니 상세의 시리즈 정보
     *
     * @param animeId 애니 ID
     */
    public List<AnimeSeriesItemResultDto> getAnimeSeries(Long animeId) {
        List<AnimeDateItemDto> animeDateItemDtos = mapper.selectAnimeInfoSeriesByAnimeId(animeId, ITEM_DEFAULT_SIZE)
                .stream()
                .map(AnimeDateItemDto::animeTitleTranslationPick)
                .toList();

        List<AnimeSeriesItemResultDto> airDateConvertItems = animeDateItemDtos.stream()
                .map(dto -> {
                    String resultAirDate;
                    LocalDate date = dto.getStartDate();

                    if (date != null) {
                        Season season = Season.containsSeason(date);
                        String seasonName = season.getName();
                        resultAirDate = date.getYear() + "년 " + seasonName;
                    } else {
                        resultAirDate = UNKNOWN_AIR_DATE;
                    }

                    return AnimeSeriesItemResultDto.of(
                            dto.getAnimeId(),
                            dto.getTitle(),
                            dto.getCoverImageUrl(),
                            resultAirDate
                    );
                })
                .toList();
        return airDateConvertItems;
    }

    /**
     * 애니 상세의 캐릭터와 성우진
     *
     * @param animeId 애니 ID
     */
    public List<AnimeCharacterActorItemPickNameDto> getAnimeInfoCharacterActor(Long animeId) {
        List<AnimeCharacterActorItemPickNameDto> items = mapper.selectAnimeInfoCharacterActors(animeId, ITEM_DEFAULT_SIZE)
                .stream()
                .map(item -> {
                    String localizedCharacterName = LocalizationUtil.pickCharacterName(
                            item.getCharacter().getNameKor(),
                            item.getCharacter().getNameEng()
                    );

                    VoiceActorDto voiceActor = item.getVoiceActor();
                    // 캐릭터만 있고, 성우만 존재하는 경우
                    if (voiceActor == null) {
                        return AnimeCharacterActorItemPickNameDto.of(
                                CharacterPickNameDto.from(
                                        item.getCharacter().getId(),
                                        localizedCharacterName,
                                        item.getCharacter().getImageUrl()
                                ),
                                VoiceActorPickNameDto.from(
                                        null,
                                        null,
                                        null
                                )
                        );
                    }

                    String localizedVoiceActorName = LocalizationUtil.pickVoiceActorName(
                            voiceActor.getNameKor(),
                            voiceActor.getNameEng()
                    );

                    return AnimeCharacterActorItemPickNameDto.of(
                            CharacterPickNameDto.from(
                                    item.getCharacter().getId(),
                                    localizedCharacterName,
                                    item.getCharacter().getImageUrl()
                            ),
                            VoiceActorPickNameDto.from(
                                    item.getVoiceActor().getId(),
                                    localizedVoiceActorName,
                                    item.getVoiceActor().getImageUrl()
                            )
                    );
                })
                .toList();
        return items;
    }

    /**
     * 해당 애니의 캐릭터와 성우진 리스트 상세
     *
     * @param animeId   애니 ID
     * @param lastId    마지막 ID (캐릭터 ID)
     * @param lastValue 캐릭터의 역할 (MAIN, SUPPORTING, BACKGROUND)
     * @param size      개수
     */
    public AnimeCharacterActorPageDto getAnimeCharacterActor(Long animeId, Long lastId, AnimeCharacterRole lastValue, int size) {
        List<AnimeCharacterActorResultDto> items = mapper.selectAnimeCharacterActors(animeId, lastId, lastValue, size);

        List<AnimeCharacterActorItemWithRoleDto> pickNameCharacterAndActors = items.stream()
                .map(dto -> {
                            // 캐릭터만 있고, 성우만 존재하는 경우
                            if (dto.getVoiceActor() == null) {
                                return AnimeCharacterActorItemWithRoleDto.of(
                                        CharacterPickNameDto.from(
                                                dto.getCharacter().getId(),
                                                LocalizationUtil.pickCharacterName(dto.getCharacter().getNameKor(),
                                                        dto.getCharacter().getNameEng()),
                                                dto.getCharacter().getImageUrl()
                                        ),
                                        VoiceActorPickNameDto.from(
                                                null,
                                                null,
                                                null
                                        ),
                                        dto.getRole()
                                );
                            }
                            return AnimeCharacterActorItemWithRoleDto.of(
                                    CharacterPickNameDto.from(
                                            dto.getCharacter().getId(),
                                            LocalizationUtil.pickCharacterName(dto.getCharacter().getNameKor(),
                                                    dto.getCharacter().getNameEng()),
                                            dto.getCharacter().getImageUrl()
                                    ),
                                    VoiceActorPickNameDto.from(
                                            dto.getVoiceActor().getId(),
                                            LocalizationUtil.pickVoiceActorName(dto.getVoiceActor().getNameKor(),
                                                    dto.getVoiceActor().getNameEng()),
                                            dto.getVoiceActor().getImageUrl()
                                    ),
                                    dto.getRole()
                            );
                        }
                )
                .toList();

        Long nextId;
        AnimeCharacterRole nextValue;

        if (pickNameCharacterAndActors.isEmpty()) {
            nextId = null;
            nextValue = null;
        } else {
            nextId = pickNameCharacterAndActors.getLast().getCharacter().getId();
            nextValue = pickNameCharacterAndActors.getLast().getRole();
        }

        String nextValueStr;
        if (nextValue == null) {
            nextValueStr = null;
        } else {
            nextValueStr = nextValue.toString();
        }

        CursorDto cursor = CursorDto.of(null, nextId, nextValueStr);
        return AnimeCharacterActorPageDto.of(cursor, pickNameCharacterAndActors);
    }

    /**
     * 해당 애니의 함께 볼만한 작품 리스트 상세
     *
     * @param animeId 애니 ID
     * @param lastId  마지막 ID (애니 ID)
     * @param size    개수
     */
    public AnimeRecommendationPageDto getRecommendationsByAnime(Long animeId, Long lastId, int size) {
        Anime anime = mapper.selectAnimeByAnimeId(animeId);

        String animeTitle = anime.getTitlePick();

        List<AnimeItemDto> items = mapper.selectRecommendationsByAnimeId(animeId, lastId, size)
                .stream()
                .map(AnimeItemDto::animeTitleTranslationPick)
                .toList();

        Long nextId;
        if (items.isEmpty()) {
            nextId = null;
        } else {
            nextId = items.getLast().getAnimeId();
        }
        CursorDto cursor = CursorDto.of(nextId);
        return AnimeRecommendationPageDto.of(animeTitle, cursor, items);
    }

    /**
     * 해당 애니의 시리즈 리스트 상세
     *
     * @param animeId 애니 ID
     * @param lastId  마지막 ID (애니 ID)
     * @param size    개수
     */
    public AnimeSeriesPageDto getSeriesByAnime(Long animeId, Long lastId, int size) {
        long totalCount = mapper.countSeriesAnime(animeId);

        List<AnimeDateItemDto> items = mapper.selectSeriesByAnimeId(animeId, lastId, size)
                .stream()
                .map(AnimeDateItemDto::animeTitleTranslationPick)
                .toList();
        List<AnimeSeriesItemResultDto> airDateConvertItems = items.stream()
                .map(dto -> {
                    String resultAirDate;
                    LocalDate date = dto.getStartDate();

                    if (date != null) {
                        Season season = Season.containsSeason(date);
                        String seasonName = season.getName();
                        resultAirDate = date.getYear() + "년 " + seasonName;
                    } else {
                        resultAirDate = UNKNOWN_AIR_DATE;
                    }

                    return AnimeSeriesItemResultDto.of(
                            dto.getAnimeId(),
                            dto.getTitle(),
                            dto.getCoverImageUrl(),
                            resultAirDate
                    );
                })
                .toList();

        Long nextId;
        if (items.isEmpty()) {
            nextId = null;
        } else {
            nextId = items.getLast().getAnimeId();
        }

        CursorDto cursor = CursorDto.of(nextId);

        return AnimeSeriesPageDto.of(totalCount, cursor, airDateConvertItems);
    }

    /**
     * 해당 애니의 내 리뷰 보기
     *
     * @param animeId 애니 ID
     * @param userId  로그인 중인 유저 ID
     */
    public AnimeMyReviewResultDto getAnimeMyReview(Long animeId, Long userId) {
        AnimeMyReviewResultDto result = mapper.selectAnimeMyReview(animeId, userId);
        if (result == null) {
            return AnimeMyReviewResultDto.empty();
        }
        String reviewCreatedAt = result.getCreatedAt();
        LocalDateTime dateTime = LocalDateTime.parse(reviewCreatedAt, parser);

        String formattedDate = dateTime.format(formatter);
        return AnimeMyReviewResultDto.createdAtFormatted(result, formattedDate);
    }

    /**
     * 공개예정 애니의 정렬이 최신순
     *
     * @param sort                 latest
     * @param comingSoonRequestDto
     * @param totalCount
     */
    private ComingSoonPageDto getSortLatestComingSoonAnimes(String sort, ComingSoonRequestDto comingSoonRequestDto, long totalCount) {
        List<ComingSoonItemBasicDto> latestSortAnimes =
                mapper.selectComingSoonLatestAnimes(comingSoonRequestDto).stream()
                        .map(ComingSoonItemBasicDto::animeTitleTranslationPick)
                        .toList();

        List<ComingSoonItemDto> items = latestSortAnimes.stream()
                .map(b -> new ComingSoonItemDto(
                        b.getAnimeId(),
                        b.getTitle(),
                        b.getCoverImageUrl(),
                        b.getStartDate(),
                        b.getIsAdult()
                ))
                .collect(Collectors.toList());

        Long nextId;

        if (items.isEmpty()) {
            nextId = null;
        } else {
            nextId = items.getLast().getAnimeId();
        }

        CursorDto cursor = CursorDto.of(sort, nextId, null);
        return ComingSoonPageDto.of(totalCount, cursor, items);
    }

    /**
     * 공개예정 애니의 정렬이 인기순
     *
     * @param sort                 popularity
     * @param comingSoonRequestDto
     * @param totalCount
     */
    private ComingSoonPageDto getSortPopularityComingSoonAnimes(String sort, ComingSoonRequestDto comingSoonRequestDto, long totalCount) {
        List<ComingSoonItemPopularityDto> popularitySortAnimes =
                mapper.selectComingSoonPopularityAnimes(comingSoonRequestDto)
                        .stream()
                        .map(ComingSoonItemPopularityDto::animeTitleTranslationPick)
                        .toList();

        Long nextId;

        if (popularitySortAnimes.isEmpty()) {
            nextId = null;
        } else {
            nextId = popularitySortAnimes.getLast().getScore();
        }

        List<ComingSoonItemDto> items = popularitySortAnimes.stream()
                .map(b -> new ComingSoonItemDto(
                        b.getAnimeId(),
                        b.getTitle(),
                        b.getCoverImageUrl(),
                        b.getStartDate(),
                        b.getIsAdult()
                ))
                .collect(Collectors.toList());

        CursorDto cursor = CursorDto.of(sort, nextId, null);
        return ComingSoonPageDto.of(totalCount, cursor, items);
    }

    /**
     * 공개예정 애니의 정렬이 방영 예정 순
     *
     * @param sort                 startDate
     * @param comingSoonRequestDto
     * @param totalCount
     */
    private ComingSoonPageDto getSortStartDateComingSoonAnimes(String sort, ComingSoonRequestDto comingSoonRequestDto, long totalCount) {
        List<ComingSoonItemAllTitleDto> starDateSortAnimes =
                mapper.selectComingSoonStartDateAnimes(comingSoonRequestDto);

        String nextValue;

        if (starDateSortAnimes.isEmpty()) {
            nextValue = null;
        } else {
            nextValue = starDateSortAnimes.getLast().getStartDate();
        }

        List<ComingSoonItemBasicDto> typeCovertAnimes = starDateSortAnimes.stream()
                .map(ComingSoonItemBasicDto::animeTitleTranslationPick)
                .collect(Collectors.toList());

        List<ComingSoonItemDto> items = typeCovertAnimes.stream()
                .map(b -> new ComingSoonItemDto(
                        b.getAnimeId(),
                        b.getTitle(),
                        b.getCoverImageUrl(),
                        b.getStartDate(),
                        b.getIsAdult()
                ))
                .collect(Collectors.toList());

        Long nextId;

        if (starDateSortAnimes.isEmpty()) {
            nextId = null;
        } else {
            nextId = starDateSortAnimes.getLast().getAnimeId();
        }
        CursorDto cursor = CursorDto.of(sort, nextId, nextValue);
        return ComingSoonPageDto.of(totalCount, cursor, items);
    }

    /**
     * 애니 상세에서 리뷰보기의 최신순
     *
     * @param totalCount
     * @param items
     * @param sort       latest
     */
    private AnimeDetailInfoReviewsPageDto getSortLatestAnimeDetailReviews(long totalCount, List<AnimeDetailInfoReviewsResultDto> items, String sort) {
        Long nextId;

        if (items.isEmpty()) {
            nextId = null;
        } else {
            nextId = items.getLast().getReviewId();
        }

        CursorDto cursor = CursorDto.of(sort, nextId);

        return AnimeDetailInfoReviewsPageDto.of(totalCount, cursor, items);
    }

    /**
     * 애니 상세에서 리뷰보기의 평점 높은 순
     *
     * @param totalCount
     * @param items
     * @param sort       ratingDesc
     */
    private AnimeDetailInfoReviewsPageDto getSortRatingDescAnimeDetailReviews(long totalCount, List<AnimeDetailInfoReviewsResultDto> items, String sort) {
        Long nextId;
        Double nextValue;

        if (items.isEmpty()) {
            nextId = null;
            nextValue = null;
        } else {
            nextId = items.getLast().getReviewId();
            nextValue = items.getLast().getRating();
        }

        String nextValueStr;
        if (nextValue == null) {
            nextValueStr = null;
        } else {
            nextValueStr = nextValue.toString();
        }

        CursorDto cursor = CursorDto.of(sort, nextId, nextValueStr);

        return AnimeDetailInfoReviewsPageDto.of(totalCount, cursor, items);
    }

    /**
     * 애니 상세에서 리뷰보기의 평점 낮은 순
     *
     * @param totalCount
     * @param items
     * @param sort       ratingAsc
     */
    private AnimeDetailInfoReviewsPageDto getSortRatingAscAnimeDetailReviews(long totalCount, List<AnimeDetailInfoReviewsResultDto> items, String sort) {
        Long nextId;
        Double nextValue;

        if (items.isEmpty()) {
            nextId = null;
            nextValue = null;
        } else {
            nextId = items.getLast().getReviewId();
            nextValue = items.getLast().getRating();
        }

        String nextValueStr;
        if (nextValue == null) {
            nextValueStr = null;
        } else {
            nextValueStr = nextValue.toString();
        }

        CursorDto cursor = CursorDto.of(sort, nextId, nextValueStr);

        return AnimeDetailInfoReviewsPageDto.of(totalCount, cursor, items);
    }

    /**
     * 애니 상세에서 리뷰보기의 좋아요 순
     *
     * @param totalCount
     * @param items
     * @param sort       likes
     */
    private AnimeDetailInfoReviewsPageDto getSortLikesAnimeDetailReviews(long totalCount, List<AnimeDetailInfoReviewsResultDto> items, String sort) {
        Long nextId;
        Long nextValue;

        if (items.isEmpty()) {
            nextId = null;
            nextValue = null;
        } else {
            nextId = items.getLast().getReviewId();
            nextValue = items.getLast().getLikeCount();
        }

        String nextValueStr;
        if (nextValue == null) {
            nextValueStr = null;
        } else {
            nextValueStr = nextValue.toString();
        }

        CursorDto cursor = CursorDto.of(sort, nextId, nextValueStr);

        return AnimeDetailInfoReviewsPageDto.of(totalCount, cursor, items);
    }

    /**
     * 유저의 프로필 사진 URL 생성
     *
     * @param imageId 유저 ID (auth_id)
     */
    private String getImageUrlEndpoint(Long imageId) {
        return ImageDefaults.IMAGE_ENDPOINT + imageId;
    }
}