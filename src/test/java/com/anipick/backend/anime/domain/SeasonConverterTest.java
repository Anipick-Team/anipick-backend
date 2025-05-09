package com.anipick.backend.anime.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SeasonConverterTest {
    @Test
    @DisplayName("3월 31일의 다음 시즌은 2분기가 나와야 한다.")
    void nextSeasonSuccess() {
        // given
        final LocalDate date = LocalDate.of(2025, 3, 31);
        // when
        Season nextSeason = Season.getNextSeason(date);
        // then
        assertEquals(nextSeason, Season.Q2);
    }

    @Test
    @DisplayName("2024년 12월 31일의 다음 시즌 범위 날짜는 2025년 01월 01일 ~ 2025년 03월 31일이다.")
    void getNextSeasonYearSuccess() {
        // given
        final LocalDate date = LocalDate.of(2024, 12, 31);
        // when
        RangeDate nextSeasonRangDate = SeasonConverter.getNextSeasonRangDate(date);
        // then
        String startDate = nextSeasonRangDate.getStartDate();
        String endDate = nextSeasonRangDate.getEndDate();

        assertEquals(startDate, LocalDate.of(2025, 1, 1).toString());
        assertEquals(endDate, LocalDate.of(2025, 3, 31).toString());
    }
}