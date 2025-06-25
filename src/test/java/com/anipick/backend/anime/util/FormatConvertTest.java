package com.anipick.backend.anime.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormatConvertTest {
    @Test
    @DisplayName("TV 입력 시 TVA 반환")
    void tvReturnsTva() {
        assertEquals("TVA", FormatConvert.toClientType("TV"));
    }

    @Test
    @DisplayName("ONA 입력 시 TVA 반환")
    void onaReturnsTva() {
        assertEquals("TVA", FormatConvert.toClientType("ONA"));
    }

    @Test
    @DisplayName("OVA 입력 시 OVA 반환")
    void ovaReturnsOva() {
        assertEquals("OVA", FormatConvert.toClientType("OVA"));
    }

    @Test
    @DisplayName("MOVIE 입력 시 극장판 반환")
    void movieReturns_movie() {
        assertEquals("극장판", FormatConvert.toClientType("MOVIE"));
    }

    @Test
    @DisplayName("지정된 값 외의 입력 시 기타 반환")
    void unknownReturns_unknown() {
        assertEquals("기타", FormatConvert.toClientType("X"));
    }
}