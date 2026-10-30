package com.anipick.backend.community.dto;

import lombok.Getter;

/**
 * 탐색 게시판 목록 조회 원본. 제목 다국어 픽과 장르 병합은 서비스에서 가공.
 * seriesTrending/seriesPopularity 는 popular, lastPostedAt 은 latest 정렬에서만 채워지며 커서(lastValue)로 쓴다.
 */
@Getter
public class ExploreBoardRawDto {
    private Long seriesId;
    private String titleMan;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;

    // popular: 시리즈 소속 애니의 MAX(trending), MAX(popularity) (실시간 랭킹 기준)
    private Long seriesTrending;
    private Long seriesPopularity;

    // latest: 가장 최근 활성 게시글 시각 (yyyy-MM-dd HH:mm:ss). 게시글 없으면 null
    private String lastPostedAt;
}
