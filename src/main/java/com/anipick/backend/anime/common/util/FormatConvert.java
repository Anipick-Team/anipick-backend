package com.anipick.backend.anime.common.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.anipick.backend.anime.domain.AnimeFormat;

public class FormatConvert {
	private static final Map<String, List<AnimeFormat>> MAP = Map.of(
		"TVA", List.of(AnimeFormat.TV, AnimeFormat.TV_SHORT, AnimeFormat.ONA),
		"OVA", List.of(AnimeFormat.OVA, AnimeFormat.SPECIAL),
		"극장판", List.of(AnimeFormat.MOVIE)
	);

	public static List<String> toConvert(String clientFromType) {
		if (clientFromType == null) return Collections.emptyList();
		return MAP.getOrDefault(clientFromType, Collections.emptyList())
			.stream()
			.map(Enum::name)
			.toList();
	}
}
