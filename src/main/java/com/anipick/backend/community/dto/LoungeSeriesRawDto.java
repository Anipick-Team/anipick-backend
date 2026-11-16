package com.anipick.backend.community.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 라운지용 애니 → 시리즈 매핑 원본. 제목 선택은 서비스에서 처리.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoungeSeriesRawDto {
    private Long animeId;
    private Long seriesId;
    private String titleMan;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;
}
