package com.anipick.backend.community.dto;

import lombok.Getter;

/**
 * anime LEFT JOIN series_anime LEFT JOIN series 조회 원본.
 * seriesId 가 null 이면 게시판(시리즈 매핑) 없음 → 애니 자체 값(anime*)으로 fallback.
 * 제목은 LocalizationUtil 로 서비스에서 픽.
 */
@Getter
public class BoardInfoRawDto {
    // 게시판(시리즈) 존재 시 사용
    private Long seriesId;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;

    // 게시판 없을 때 팝업용 fallback (애니 자체 정보)
    private String animeTitleMan;
    private String animeTitleKor;
    private String animeTitleEng;
    private String animeTitleRom;
    private String animeTitleNat;
    private String animeCoverImageUrl;
}
