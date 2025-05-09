package com.anipick.backend.anime.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Season {
    /**
     * WINTER(Q1) (1분기): 12월~2월 -> 1월 ~ 3월
     * SPRING(Q2) (2분기): 3월~5월 -> 4월 ~ 6월
     * SUMMER(Q3) (3분기): 6월~8월 -> 7월 ~ 9월
     * FALL  (Q4) (4분기): 9월~11월 -> 10월 ~ 12월
     */
    Q1(1, "1분기", Month.JANUARY, Month.MARCH),
    Q2(2, "2분기", Month.APRIL, Month.JUNE),
    Q3(3, "3분기", Month.JULY, Month.SEPTEMBER),
    Q4(4, "4분기", Month.OCTOBER, Month.DECEMBER);

    private final static Map<Integer, Season> codeToEnumMap = Arrays.stream(values())
            .collect(Collectors.toMap(Season::getCode, Function.identity()));
    private final int code;
    private final String name;
    private final Month startMonth;
    private final Month endMonth;

    public static Season getNextSeason(LocalDate now) {
        final Season[] values = values();
        final Season currentSeason = Arrays.stream(values)
                .filter(season -> season.contains(now))
                .findFirst()
                .orElseThrow();
        return currentSeason.next();
    }

    public static Season getByCode(int code) {
        return codeToEnumMap.get(code);
    }

    public boolean contains(LocalDate date) {
        int month = date.getMonthValue();
        return switch (this) {
            case Q1 -> (month >= 1 && month <= 3);
            case Q2 -> (month >= 4 && month <= 6);
            case Q3 -> (month >= 7 && month <= 9);
            case Q4 -> (month >= 10 && month <= 12);
        };
    }

    private Season next() {
        Season[] seasons = values();
        return seasons[(this.ordinal() + 1) % seasons.length];
    }

    // 날짜 -> 분기
    public static Season containsSeason(LocalDate date) {
        return Arrays.stream(values())
                .filter(season -> season.contains(date))
                .findFirst()
                .orElseThrow();
    }
}