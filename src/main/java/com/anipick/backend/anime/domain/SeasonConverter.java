package com.anipick.backend.anime.domain;

import java.time.LocalDate;
import java.time.YearMonth;

public class SeasonConverter {

    // 년도만 눌렀을 때의 범위 날짜 (탐색용)
    public static RangeDate getYearRangDate(int year) {
        final String startDate = "%d-01-01".formatted(year);
        final String endDate = "%d-12-31".formatted(year);
        return new RangeDate(startDate, endDate);
    }

    // 년도+분기 눌렀을 때의 범위 날짜 (탐색용)
    public static RangeDate getRangDate(int year, int quarter) {
        final Season nextSeason = Season.getByCode(quarter);
        final int startMonth = nextSeason.getStartMonth().getValue();
        final int endMonth = nextSeason.getEndMonth().getValue();
        final int lastDate = getLastDate(year, endMonth);
        final String startDate = "%d-%02d-01".formatted(year, startMonth);
        final String endDate = "%d-%02d-%02d".formatted(year, endMonth, lastDate);
        return new RangeDate(startDate, endDate);
    }

    // 현재 날짜를 기준으로 다음 분기의 범위 날짜 (방영예정용)
    public static RangeDate getNextSeasonRangDate(LocalDate now) {
        final Season nextSeason = Season.getNextSeason(now); // 다음 시즌이 잘 나오는지 테스트
        final int year = getYear(now, nextSeason); // 다음 해로 넘어가는지 테스트
        final int startMonth = nextSeason.getStartMonth().getValue();
        final int endMonth = nextSeason.getEndMonth().getValue();
        final int lastDate = getLastDate(year, endMonth);
        final String startDate = "%d-%02d-01".formatted(year, startMonth);
        final String endDate = "%d-%02d-%02d".formatted(year, endMonth, lastDate);
        return new RangeDate(startDate, endDate);
    }

    private static int getLastDate(final int year, final int endMonth) {
        final YearMonth yearMonth = YearMonth.of(year, endMonth);
        final int lastDate = yearMonth.atEndOfMonth().getDayOfMonth();
        return lastDate;
    }

    private static int getYear(final LocalDate now, final Season nextSeasonDate) {
        if (nextSeasonDate == Season.Q1) {
            return now.getYear() + 1;
        }
        return now.getYear();
    }
}
