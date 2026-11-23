package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 다 본 애니 목록의 커서.
 * lastValue 형식 - latest: "yyyy-MM-dd HH:mm:ss", startDate: "yyyy-MM-dd"(방영 미정이면 없음),
 * ratingDesc/ratingAsc: "평점,yyyy-MM-dd HH:mm:ss"(평가 없으면 평점 자리 비움)
 * sortedAt 은 평가한 시간(review.updated_at), 평가가 없으면 다 본 애니가 된 시간(useranimestatus.updated_at).
 */
@Getter
@AllArgsConstructor
public class FinishedAnimesCursorDto {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String RATING_SEPARATOR = ",";

    private String sortOption;
    private Long lastId;
    private LocalDateTime lastSortedAt;
    private LocalDate lastStartDate;
    private Double lastRating;

    /**
     * 다 본 애니에서 지원하는 정렬은 latest, startDate, ratingDesc, ratingAsc. 그 외는 400 에러.
     * lastId 가 없으면 첫 페이지로 보고 lastValue 는 무시한다.
     */
    public static FinishedAnimesCursorDto of(SortOption sortOption, Long lastId, String lastValue) {
        String code = sortOption.getCode();

        try {
            return switch (sortOption) {
                case LATEST -> lastId == null
                        ? firstPage(code)
                        : new FinishedAnimesCursorDto(code, lastId, parseDateTime(lastValue), null, null);
                case START_DATE -> lastId == null
                        ? firstPage(code)
                        : new FinishedAnimesCursorDto(code, lastId, null, lastValue == null ? null : LocalDate.parse(lastValue), null);
                case RATING_DESC, RATING_ASC -> lastId == null
                        ? firstPage(code)
                        : ratingCursor(code, lastId, lastValue);
                default -> throw new CustomException(ErrorCode.BAD_REQUEST);
            };
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * 응답 cursor.lastValue. 값이 없으면 null (응답에서 키 생략).
     */
    public static String toLastValue(SortOption sortOption, FinishedAnimesAllTitleDto last) {
        return switch (sortOption) {
            case START_DATE -> last.getStartDate() == null ? null : last.getStartDate().toString();
            case RATING_DESC, RATING_ASC -> {
                String rating = last.getMyRating() == null ? "" : String.valueOf(last.getMyRating());
                yield rating + RATING_SEPARATOR + last.getSortedAt().format(DATE_TIME_FORMAT);
            }
            default -> last.getSortedAt().format(DATE_TIME_FORMAT);
        };
    }

    private static FinishedAnimesCursorDto firstPage(String code) {
        return new FinishedAnimesCursorDto(code, null, null, null, null);
    }

    private static FinishedAnimesCursorDto ratingCursor(String code, Long lastId, String lastValue) {
        String[] values = requireValue(lastValue).split(RATING_SEPARATOR, -1);
        if (values.length != 2) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        Double rating = values[0].isEmpty() ? null : Double.valueOf(values[0]);
        return new FinishedAnimesCursorDto(code, lastId, parseDateTime(values[1]), null, rating);
    }

    private static LocalDateTime parseDateTime(String value) {
        return LocalDateTime.parse(requireValue(value), DATE_TIME_FORMAT);
    }

    private static String requireValue(String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return value;
    }
}
