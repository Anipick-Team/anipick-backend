package com.anipick.backend.user.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NicknameInitializer {
    private static final List<String> PREFIX_WORDS = List.of(
            "푸른", "달콤한", "반짝이는", "작은", "하얀", "노란", "새벽의", "기쁜", "조용한",
            "따뜻한", "웃는", "느린", "붉은", "맑은", "서늘한", "빛나는", "행복한", "외로운",
            "흐린", "살랑이는", "잔잔한", "고요한", "은은한", "순수한", "우아한", "그리운",
            "외로운", "이른", "끝없는", "가벼운"
    );
    private static final List<String> SUFFIX_WORDS = List.of(
            "별", "햇살", "달", "구름", "바람", "고래", "소나기", "눈꽃", "하늘", "노을",
            "토끼", "나비", "강아지", "숲", "연못", "파도", "산책", "행성", "초승달", "파랑",
            "눈보라", "물결", "조약돌", "여우", "벚꽃", "무지개", "고양이", "시냇물", "진달래", "별빛"
    );
    private static final int NICKNAME_MAX_LENGTH = 20;
    private static final Random RANDOM = new Random();
    private static final String WORD_SPACE = " ";

    public static String generateNickname() {
        String prefix = PREFIX_WORDS.get(RANDOM.nextInt(PREFIX_WORDS.size()));
        String suffix = SUFFIX_WORDS.get(RANDOM.nextInt(SUFFIX_WORDS.size()));
        String base = prefix + WORD_SPACE + suffix;

        int maxDigits = NICKNAME_MAX_LENGTH - base.length();
        String number = RANDOM.ints(RANDOM.nextInt(Math.min(8, maxDigits) + 1), 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());

        return base + number;
    }
}
