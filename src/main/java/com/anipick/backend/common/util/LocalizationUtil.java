package com.anipick.backend.common.util;

public class LocalizationUtil {
    /**
     * @param korTitle 번역
     * @param engTitle 영어
     * @param romajTitle 로마자
     * @param nativeTitle 원어
     * @return 우선순위 번역-영어-로마자-원어 (전부 null일 경우, "제목 정보 없음" 리턴)
     */
    public static String pickTitle(String korTitle, String engTitle, String romajTitle, String nativeTitle) {
        if (korTitle != null && !korTitle.isBlank()) {
            return korTitle;
        }
        if (engTitle != null && !engTitle.isBlank()) {
            return engTitle;
        }
        if (romajTitle != null && !romajTitle.isBlank()) {
            return romajTitle;
        }
        if (nativeTitle != null && !nativeTitle.isBlank()) {
            return nativeTitle;
        }
        return "제목 정보 없음";
    }

    /**
     * @param korName 번역
     * @param engName 영어
     * @return 우선순위 번역-영어 (전부 null일 경우, "제작사 이름 정보 없음" 리턴)
     */
    public static String pickStudioName(String korName, String engName) {
        if (korName != null && !korName.isBlank()) {
            return korName;
        }
        if (engName != null && !engName.isBlank()) {
            return engName;
        }
        return "제작사 이름 정보 없음";
    }

    /**
     * @param korName 번역
     * @param engName 영어
     * @return 우선순위 번역-영어 (전부 null일 경우, "캐릭터 이름 정보 없음" 리턴)
     */
    public static String pickCharacterName(String korName, String engName) {
        if (korName != null && !korName.isBlank()) {
            return korName;
        }
        if (engName != null && !engName.isBlank()) {
            return engName;
        }
        return "캐릭터 이름 정보 없음";
    }

    /**
     * @param korName 번역
     * @param engName 영어
     * @return 우선순위 번역-영어 (전부 null일 경우, "성우 이름 정보 없음" 리턴)
     */
    public static String pickVoiceActorName(String korName, String engName) {
        if (korName != null && !korName.isBlank()) {
            return korName;
        }
        if (engName != null && !engName.isBlank()) {
            return engName;
        }
        return "성우 이름 정보 없음";
    }
}
