package com.anipick.backend.anime.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.anipick.backend.anime.util.LocalizationUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Anime {
	private Long animeId;
	private String titleKor;
	private String titleEng;
	private String titleRom;
	private String titleNat;
	private String coverImageUrl;
	private String bannerImageUrl;
	private String descriptionKor;
	private LocalDate startDate;
	private LocalDate endDate;
	// 애니리스트의 분기는 우리나라와 맞지 않아서 season_int 는 X
	private Long seasonYear;
	private Long episodeCount;
	private Long duration;
	private AnimeStatus status;
	private Double averageScore;
	private Long popularity;
	private Boolean isAdult;
	private AnimeFormat format;
	private Long reviewCount;
	// 다음 에피소드 방영일
	private LocalDateTime nextAiringAt;
	// 다음 에피소드 방영일까지 남은 시간
	private String nextTimeUntilAiring;
	// 다음 에피소드
	private Long nextEpisode;

	public String getTitlePick() {
		return LocalizationUtil.pickTitle(
                this.getTitleKor(),
                this.getTitleEng(),
                this.getTitleRom(),
                this.getTitleNat()
        );
	}
}
