package com.anipick.backend.anime.service.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComingSoonItemBasicDtoTest {

    @Test
    @DisplayName("방영 시작날이 null일 때, `미정`으로 표시한다.")
    void startDateNullToUnknown() {
        // given
        ComingSoonItemBasicDto dto =
                new ComingSoonItemBasicDto(
                        1L,
                        "타이틀",
                        "url",
                        null,
                        "TV",
                        false
                );
        // then
        dto.typeToReleaseDate();
        // when
        assertEquals("미정", dto.getStartDate());
    }

    @Test
    @DisplayName("SPECIAL 포맷인 경우 `yyyy. mm. dd`으로 표시한다.")
    void specialFormatToYyyyMmDd() {
        // given
        String date = "2022-02-01";
        // SPECIAL 일 경우
        ComingSoonItemBasicDto dtoSpecial =
                new ComingSoonItemBasicDto(
                        1L,
                        "스페샬 제목",
                        "img.jpg",
                        date,
                        "SPECIAL",
                        false
                );
        // when
        dtoSpecial.typeToReleaseDate();

        // given
        assertEquals("2022. 02. 01", dtoSpecial.getStartDate());
    }

    @Test
    @DisplayName("OVA 포맷인 경우 `yyyy. mm. dd`으로 표시한다.")
    void ovaFormatToYyyyMmDd() {
        // given
        String date = "2022-02-01";
        ComingSoonItemBasicDto dtoOva =
                new ComingSoonItemBasicDto(
                        1L,
                        "OVA 제목이에요",
                        "img.png",
                        date,
                        "OVA",
                        false
                );
        // when
        dtoOva.typeToReleaseDate();
        // then
        assertEquals("2022. 02. 01", dtoOva.getStartDate());
    }

    @Test
    @DisplayName("MOVIE 포맷인 경우 `yyyy. mm. dd`으로 표시한다.")
    void movieFormatToYyyyMmDd() {
        // given
        String date = "2022-02-01";
        ComingSoonItemBasicDto dtoOva =
                new ComingSoonItemBasicDto(
                        1L,
                        "극장판 제목이야",
                        "default.jpg",
                        date,
                        "MOVIE",
                        false
                );
        // when
        dtoOva.typeToReleaseDate();
        // then
        assertEquals("2022. 02. 01", dtoOva.getStartDate());
    }

    @Test
    @DisplayName("1월부터 3월인 경우, `XX년 1분기`로 표시한다.")
    void tvFormatYYQuarterQ1() {
        // given
        String date = "2022-03-01";
        ComingSoonItemBasicDto dto =
                new ComingSoonItemBasicDto(
                        1L,
                        "TV 포맷의 1분기",
                        "iqwer.jpg",
                        date,
                        "TV",
                        false
                );
        // when
        dto.typeToReleaseDate();
        // then
        assertEquals("22년 1분기", dto.getStartDate());
    }

    @Test
    @DisplayName("4월부터 6월인 경우, `XX년 2분기`로 표시한다.")
    void tvShortFormatYYQuarterQ2() {
        // given
        String date = "2022-05-10";
        ComingSoonItemBasicDto dto =
                new ComingSoonItemBasicDto(
                        1L,
                        "TV_SHORT는 잘 나올까",
                        "default.jpg",
                        date,
                        "TV_SHORT",
                        false
                );
        // when
        dto.typeToReleaseDate();
        // then
        assertEquals("22년 2분기", dto.getStartDate());
    }

    @Test
    @DisplayName("7월부터 9월인 경우, `XX년 3분기`로 표시한다.")
    void onaFormatYYQuarterQ3() {
        // given
        String date = "2022-08-01";
        ComingSoonItemBasicDto dto =
                new ComingSoonItemBasicDto(
                        1L,
                        "ONA도 잘 나오길",
                        "qwer.jpg",
                        date,
                        "ONA",
                        false
                );
        // when
        dto.typeToReleaseDate();
        // then
        assertEquals("22년 3분기", dto.getStartDate());
    }

    @Test
    @DisplayName("10월부터 12월인 경우, `XX년 4분기`로 표시한다.")
    void tvFormatYYQuarterQ4() {
        // given
        String date = "2022-11-20";
        ComingSoonItemBasicDto dto =
                new ComingSoonItemBasicDto(
                        1L,
                        "TV의 4분기는",
                        "23.jpg",
                        date,
                        "TV",
                        false
                );
        // when
        dto.typeToReleaseDate();
        // then
        assertEquals("22년 4분기", dto.getStartDate());
    }
}