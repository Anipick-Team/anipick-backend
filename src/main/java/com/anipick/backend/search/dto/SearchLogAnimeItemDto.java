package com.anipick.backend.search.dto;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.log.domain.Area;
import com.anipick.backend.log.domain.DefaultDataBody;
import com.anipick.backend.log.domain.Page;
import com.anipick.backend.log.domain.UserActionLog;
import com.anipick.backend.log.utils.UrlSafeObjectEncoder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class SearchLogAnimeItemDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private String clickLog;
    private String impressionLog;

    public static SearchLogAnimeItemDto from(
            AnimeItemDto item,
            int positionNumber,
            String logBaseUrl
    ) {
        DefaultDataBody logDataBody = DefaultDataBody.createAnimeData(item.getTitle(), positionNumber);

        UserActionLog userActionClickLog = UserActionLog.createClickLog(Page.SEARCH, Area.ITEM, logDataBody);
        UserActionLog userActionImpressionLog = UserActionLog.createImpressionLog(Page.SEARCH, Area.ITEM, logDataBody);

        String encodeClickLogStr = UrlSafeObjectEncoder.encodeURL(userActionClickLog);
        String encodeImpressionLogStr = UrlSafeObjectEncoder.encodeURL(userActionImpressionLog);

        String clickLogUrl = logBaseUrl + encodeClickLogStr;
        String impressionLogUrl = logBaseUrl + encodeImpressionLogStr;

        return of(
                item.getAnimeId(),
                item.getTitle(),
                item.getCoverImageUrl(),
                clickLogUrl,
                impressionLogUrl
        );
    }
}
