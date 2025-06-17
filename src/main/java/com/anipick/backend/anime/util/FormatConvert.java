package com.anipick.backend.anime.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.anipick.backend.anime.domain.AnimeFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormatConvert {
	private static final Map<String, List<AnimeFormat>> MAP = Map.of(
		"TVA", List.of(AnimeFormat.TV, AnimeFormat.TV_SHORT, AnimeFormat.ONA),
		"OVA", List.of(AnimeFormat.OVA, AnimeFormat.SPECIAL),
		"극장판", List.of(AnimeFormat.MOVIE)
	);

    private static final Map<AnimeFormat, String> REVERSE_CLIENT_MAP = Map.of(
            AnimeFormat.TV, "TVA",
            AnimeFormat.TV_SHORT, "TVA",
            AnimeFormat.ONA, "TVA",
            AnimeFormat.OVA, "OVA",
            AnimeFormat.SPECIAL, "OVA",
            AnimeFormat.MOVIE, "극장판"
    );

	public static List<String> toConvert(String clientFromType) {
		if (clientFromType == null) {
			return Collections.emptyList();
		}
		return MAP.getOrDefault(clientFromType, Collections.emptyList())
			.stream()
			.map(Enum::name)
			.toList();
	}

    public static String toClientType(String formatName) {
        if (formatName == null) {
            return null;
        }
        try {
            AnimeFormat format = AnimeFormat.valueOf(formatName);
            return REVERSE_CLIENT_MAP.get(format);
        } catch (IllegalArgumentException e) {
			log.error("Anime toClientType method error format : {}, return 기타", formatName);
            return "기타";
        }
    }
}
