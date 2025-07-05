package com.anipick.backend.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class RecentHighCountOnlyRequest {
    private Long userId;
	private Long referenceAnimeId;
	private List<Long> tagIds;
	private Long lastCount;
	private Long lastId;
	private Long size;
}
